package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.entity.*;
import com.luonvuituoi.shop.event.PosHoaDonThanhToanEvent;
import com.luonvuituoi.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BanHangService {
    public static final String CHO_THANH_TOAN = "Chờ thanh toán";
    public static final String DA_HOAN_THANH = "Đã hoàn thành";
    public static final String DA_HUY = "Đã hủy";

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository chiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final GiamGiaRepository giamGiaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public HoaDon taoHoaDon(Integer nhanVienId) {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon("POS" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 4).toUpperCase(Locale.ROOT));
        hd.setNgayTao(LocalDateTime.now());
        hd.setTongTien(BigDecimal.ZERO);
        hd.setTienTruocGiam(BigDecimal.ZERO);
        hd.setPhiGiaoHang(BigDecimal.ZERO);
        hd.setHinhThucMuaHang(true);
        hd.setPhuongThucThanhToan(false);
        hd.setTrangThai(CHO_THANH_TOAN);
        if (nhanVienId != null) {
            NhanVien nv = nhanVienRepository.findById(nhanVienId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên."));
            hd.setNhanVien(nv);
        }
        return hoaDonRepository.save(hd);
    }

    @Transactional
    public void themSanPham(Integer hoaDonId, Integer sanPhamChiTietId, Integer soLuong) {
        if (soLuong == null || soLuong < 1) throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        HoaDon hd = layHoaDonDangCho(hoaDonId);
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(sanPhamChiTietId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biến thể sản phẩm."));
        if (Boolean.FALSE.equals(spct.getTrangThai()) || Boolean.FALSE.equals(spct.getSanPham().getTrangThai()))
            throw new IllegalArgumentException("Sản phẩm đã ngừng kinh doanh.");
        HoaDonChiTiet dong = chiTietRepository.findByHoaDonId(hoaDonId).stream()
                .filter(x -> x.getSanPhamChiTiet().getId().equals(sanPhamChiTietId)).findFirst().orElse(null);
        int soLuongMoi = soLuong + (dong == null ? 0 : dong.getSoLuong());
        kiemTraTonKho(spct, soLuongMoi);
        if (dong == null) {
            dong = new HoaDonChiTiet();
            dong.setHoaDon(hd);
            dong.setSanPhamChiTiet(spct);
            dong.setMaHoaDonChiTiet(hd.getMaHoaDon() + "-" + spct.getId());
            dong.setDonGia(spct.getGiaBan());
        }
        dong.setSoLuong(soLuongMoi);
        chiTietRepository.save(dong);
        tinhLaiTien(hd);
    }

    @Transactional
    public void capNhatSoLuong(Integer hoaDonId, Integer chiTietId, Integer soLuong) {
        if (soLuong == null || soLuong < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        if (!CHO_THANH_TOAN.equals(hoaDon.getTrangThai())) {
            throw new RuntimeException("Hóa đơn không còn chờ thanh toán");
        }
        HoaDonChiTiet chiTiet = chiTietRepository.findById(chiTietId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        if (!chiTiet.getHoaDon().getId().equals(hoaDonId)) {
            throw new RuntimeException("Sản phẩm không thuộc hóa đơn này");
        }
        kiemTraTonKho(chiTiet.getSanPhamChiTiet(), soLuong);
        chiTiet.setSoLuong(soLuong);
        chiTietRepository.save(chiTiet);
        tinhLaiTien(hoaDon);
    }

    @Transactional
    public void xoaSanPham(Integer hoaDonId, Integer chiTietId) {
        HoaDon hd = layHoaDonDangCho(hoaDonId);
        HoaDonChiTiet dong = chiTietRepository.findById(chiTietId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dòng sản phẩm."));
        if (!dong.getHoaDon().getId().equals(hoaDonId)) throw new IllegalArgumentException("Sản phẩm không thuộc hóa đơn này.");
        chiTietRepository.delete(dong);
        tinhLaiTien(hd);
    }

    @Transactional
    public void ganKhachHang(Integer hoaDonId, Integer khachHangId) {
        HoaDon hd = layHoaDonDangCho(hoaDonId);
        if (khachHangId == null) hd.setKhachHang(null);
        else hd.setKhachHang(khachHangRepository.findById(khachHangId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng.")));
        hoaDonRepository.save(hd);
    }

    @Transactional
    public KhachHang themKhachHangNhanh(String hoTen, String soDienThoai, String email) {
        if (hoTen == null || hoTen.isBlank()) throw new IllegalArgumentException("Vui lòng nhập họ tên khách hàng.");
        if (soDienThoai != null && !soDienThoai.isBlank() && khachHangRepository.findBySoDienThoai(soDienThoai.trim()).isPresent())
            throw new IllegalArgumentException("Số điện thoại đã được đăng ký.");
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang("KHPOS" + System.currentTimeMillis());
        kh.setHoTen(hoTen.trim());
        kh.setSoDienThoai(soDienThoai == null || soDienThoai.isBlank() ? null : soDienThoai.trim());
        kh.setEmail(email == null || email.isBlank() ? null : email.trim());
        kh.setTrangThai(true);
        kh.setNgayTao(LocalDateTime.now());
        return khachHangRepository.save(kh);
    }

    @Transactional
    public void apDungVoucher(Integer hoaDonId, String maVoucher) {
        HoaDon hd = layHoaDonDangCho(hoaDonId);
        if (maVoucher == null || maVoucher.isBlank()) {
            hd.setGiamGia(null);
            tinhLaiTien(hd);
            return;
        }
        GiamGia gg = giamGiaRepository.findByMaGiamGiaAndTrangThaiTrue(maVoucher.trim())
                .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại hoặc đã ngừng hoạt động."));
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(gg.getNgayBatDau()) || now.isAfter(gg.getNgayKetThuc())) throw new IllegalArgumentException("Voucher chưa bắt đầu hoặc đã hết hạn.");
        if (gg.getSoLuong() == null || gg.getSoLuong() <= 0) throw new IllegalArgumentException("Voucher đã hết lượt sử dụng.");
        hd.setGiamGia(gg);
        tinhLaiTien(hd);
    }

    @Transactional(readOnly = true)
    public BigDecimal tinhTienGiam(HoaDon hd) {
        if (hd.getGiamGia() == null) return BigDecimal.ZERO;
        BigDecimal truocGiam = hd.getTienTruocGiam() == null ? BigDecimal.ZERO : hd.getTienTruocGiam();
        GiamGia gg = hd.getGiamGia();
        if (gg.getGiaTriToiThieu() != null && truocGiam.compareTo(gg.getGiaTriToiThieu()) < 0)
            throw new IllegalArgumentException("Đơn hàng chưa đạt giá trị tối thiểu của voucher.");
        BigDecimal giam = gg.getLoaiGiamGia() != null && gg.getLoaiGiamGia() == 1
                ? truocGiam.multiply(gg.getGiaTriGiam()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : gg.getGiaTriGiam();
        if (gg.getToiDaGiamGia() != null && gg.getToiDaGiamGia().compareTo(BigDecimal.ZERO) > 0 && giam.compareTo(gg.getToiDaGiamGia()) > 0) giam = gg.getToiDaGiamGia();
        if (giam.compareTo(truocGiam) > 0) giam = truocGiam;
        return giam.max(BigDecimal.ZERO);
    }

    @Transactional
    public HoaDon thanhToanTienMat(Integer hoaDonId, BigDecimal tienKhachDua) {
        HoaDon hd = layHoaDonDangCho(hoaDonId);
        kiemTraCoSanPham(hd);
        tinhLaiTien(hd);
        if (tienKhachDua == null || tienKhachDua.compareTo(hd.getTongTien()) < 0) throw new IllegalArgumentException("Tiền khách đưa chưa đủ.");
        BigDecimal tienThua = tienKhachDua.subtract(hd.getTongTien());
        hd.setPhuongThucThanhToan(false);
        hd.setTrangThai(DA_HOAN_THANH);
        hd.setGhiChu(ghiChuThanhToan(hd.getGhiChu(), "Tiền mặt; khách đưa " + tienKhachDua.toPlainString() + "; tiền thừa " + tienThua.toPlainString()));
        truLuotVoucherNeuCo(hd);
        HoaDon saved = hoaDonRepository.save(hd);
        eventPublisher.publishEvent(new PosHoaDonThanhToanEvent(saved.getId()));
        return saved;
    }

    @Transactional
    public HoaDon xacNhanDaNhanChuyenKhoan(Integer hoaDonId) {
        HoaDon hd = layHoaDonDangCho(hoaDonId);
        kiemTraCoSanPham(hd);
        tinhLaiTien(hd);
        hd.setPhuongThucThanhToan(true);
        hd.setTrangThai(DA_HOAN_THANH);
        hd.setGhiChu(ghiChuThanhToan(hd.getGhiChu(), "Chuyển khoản QR - nhân viên đã xác nhận nhận tiền"));
        truLuotVoucherNeuCo(hd);
        HoaDon saved = hoaDonRepository.save(hd);
        eventPublisher.publishEvent(new PosHoaDonThanhToanEvent(saved.getId()));
        return saved;
    }

    @Transactional
    public void huyHoaDon(Integer hoaDonId) {
        HoaDon hd = layHoaDonDangCho(hoaDonId);
        hd.setTrangThai(DA_HUY);
        hoaDonRepository.save(hd);
    }

    @Transactional(readOnly = true)
    public List<HoaDonChiTiet> layChiTiet(Integer hoaDonId) { return chiTietRepository.findByHoaDonId(hoaDonId); }

    private HoaDon layHoaDonDangCho(Integer id) {
        HoaDon hd = hoaDonRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn."));
        if (!Boolean.TRUE.equals(hd.getHinhThucMuaHang()) || !CHO_THANH_TOAN.equals(hd.getTrangThai()))
            throw new IllegalArgumentException("Hóa đơn không còn ở trạng thái chờ thanh toán.");
        return hd;
    }

    private void kiemTraTonKho(SanPhamChiTiet spct, int soLuong) {
        int ton = spct.getSoLuongTon() == null ? 0 : spct.getSoLuongTon();
        if (soLuong > ton) throw new IllegalArgumentException("Số lượng vượt tồn kho hiện tại (còn " + ton + ").");
    }

    private void kiemTraCoSanPham(HoaDon hd) {
        if (chiTietRepository.findByHoaDonId(hd.getId()).isEmpty()) throw new IllegalArgumentException("Hóa đơn chưa có sản phẩm.");
    }

    private void tinhLaiTien(HoaDon hd) {
        List<HoaDonChiTiet> ds = chiTietRepository.findByHoaDonId(hd.getId());
        BigDecimal tamTinh = ds.stream().map(x -> x.getDonGia().multiply(BigDecimal.valueOf(x.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        hd.setTienTruocGiam(tamTinh);
        BigDecimal giam = tinhTienGiam(hd);
        hd.setTongTien(tamTinh.subtract(giam).max(BigDecimal.ZERO));
        hoaDonRepository.save(hd);
    }

    private void truLuotVoucherNeuCo(HoaDon hd) {
        if (hd.getGiamGia() != null) {
            GiamGia voucher = giamGiaRepository.findById(hd.getGiamGia().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Voucher không còn tồn tại."));
            if (voucher.getSoLuong() == null || voucher.getSoLuong() <= 0)
                throw new IllegalArgumentException("Voucher vừa hết lượt sử dụng, vui lòng kiểm tra lại.");
            voucher.setSoLuong(voucher.getSoLuong() - 1);
            giamGiaRepository.save(voucher);
        }
    }

    private String ghiChuThanhToan(String ghiChuCu, String noiDung) {
        String ketQua = (ghiChuCu == null || ghiChuCu.isBlank()) ? noiDung : ghiChuCu + " | " + noiDung;
        return ketQua.length() > 255 ? ketQua.substring(0, 255) : ketQua;
    }
}
