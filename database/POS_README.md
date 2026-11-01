# Module POS - Luon Vui Tuoi Shop

## 1. Framework va duong dan

- Spring Boot 3.3.4, Java 17, Spring Data JPA, Thymeleaf, SQL Server.
- Man hinh POS: `http://localhost:8080/admin/ban-hang`
- Danh sach hoa don: `http://localhost:8080/admin/hoa-don`
- Chi tiet/in hoa don: `/admin/hoa-don/{id}`. Nut In su dung hop thoai in cua trinh duyet; co the chon Save as PDF.

## 2. Cau hinh VietQR

Mo `src/main/resources/application.properties` va dien dung thong tin tai khoan cua cua hang:

```properties
pos.qr.bank-id=VCB
pos.qr.account-number=SO_TAI_KHOAN_CUA_HANG
pos.qr.account-name=TEN_CHU_TAI_KHOAN
```

Thay `VCB`, so tai khoan va ten chu tai khoan bang thong tin that. Khong de thong tin mau khi chay that. Ma QR duoc tao qua dich vu anh VietQR. He thong khong tu dong xac minh tien vao tai khoan; nhan vien phai kiem tra ung dung ngan hang truoc khi bam Xac nhan da nhan chuyen khoan.

## 3. Nghiep vu hien co

- Tao nhieu hoa don POS o trang thai `Chờ thanh toán`.
- Them bien the san pham, cap nhat/xoa so luong; kiem tra ton kho hien tai truoc khi them/cap nhat.
- Gan khach hang co san hoac tao nhanh khach hang moi.
- Ap dung/huy voucher; ho tro `loai_giam_gia = 1` la phan tram, `2` la so tien; kiem tra thoi han, so luot, gia tri don toi thieu va muc giam toi da.
- Thanh toan tien mat, tinh tien thua; thanh toan QR bang cach hien thi ma QR va nhan vien xac nhan sau khi doi soat.
- Xem danh sach, chi tiet va in hoa don.

## 4. Tich hop ton kho voi thanh vien phu trach

Module POS **khong tu sua `san_pham_chi_tiet.so_luong_ton`** theo yeu cau phan cong. Sau khi hoa don thanh toan thanh cong, `BanHangService` phat event `PosHoaDonThanhToanEvent(hoaDonId)`.

Thanh vien ton kho can lang nghe event nay va tru ton trong transaction rieng, vi du:

```java
@Component
@RequiredArgsConstructor
public class PosTonKhoListener {
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void khiPosThanhToanThanhCong(PosHoaDonThanhToanEvent event) {
        // Lay chi tiet hoa don theo event.hoaDonId(),
        // sau do tru so_luong_ton cho tung san_pham_chi_tiet.
        // Can kiem tra ton kho lai trong transaction va xu ly neu ton da thay doi.
    }
}
```

Day la diem tich hop; can thay phan than listener bang ham tru kho cua thanh vien ton kho. Khong tao them listener thu hai neu module ton kho da co co che nhan event tuong duong, tranh tru kho hai lan.

## 5. Database

Khong them cot hay doi ten bang trong ban nay. Module dung cac cot da co trong `hoa_don`, `hoa_don_chi_tiet`, `giam_gia`. Tien khach dua va tien thua duoc ghi vao `hoa_don.ghi_chu` vi schema hien tai chua co cot rieng.

## 6. Luu y khi chay

- Dam bao SQL Server dang chay va `application.properties` tro dung database.
- Du lieu mau can co san san pham, bien the, mau sac, kich thuoc; bien the phai co `trang_thai = 1` va `so_luong_ton > 0`.
- Nhan vien trong danh sach POS duoc lay tu bang `nhan_vien` voi `trang_thai = 1`.
- Chua co xac thuc quyen truy cap trong module moi; can bao ve cac URL `/admin/**` theo co che dang nhap/phan quyen chung cua du an neu du an da co.
