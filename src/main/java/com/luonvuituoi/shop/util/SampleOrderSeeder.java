package com.luonvuituoi.shop.util;

import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.entity.HoaDonChiTiet;
import com.luonvuituoi.shop.entity.KhachHang;
import com.luonvuituoi.shop.entity.NhanVien;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.repository.HoaDonChiTietRepository;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.repository.NhanVienRepository;
import com.luonvuituoi.shop.repository.SanPhamChiTietRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class SampleOrderSeeder implements CommandLineRunner {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedCustomers();
        seedOrders();
        adjustLowStockVariants();
    }

    private void seedCustomers() {
        if (khachHangRepository.count() < 5) {
            List<KhachHang> newCustomers = List.of(
                    KhachHang.builder()
                            .maKhachHang("KH03")
                            .hoTen("Trần Hoàng Nam")
                            .gioiTinh(true)
                            .ngaySinh(LocalDate.of(2001, 5, 12))
                            .email("nam.tran@gmail.com")
                            .soDienThoai("0981122334")
                            .matKhau("123456")
                            .trangThai(true)
                            .ngayTao(LocalDateTime.now().minusDays(30))
                            .build(),
                    KhachHang.builder()
                            .maKhachHang("KH04")
                            .hoTen("Phạm Quốc Bảo")
                            .gioiTinh(true)
                            .ngaySinh(LocalDate.of(1999, 8, 24))
                            .email("bao.pham@gmail.com")
                            .soDienThoai("0978998877")
                            .matKhau("123456")
                            .trangThai(true)
                            .ngayTao(LocalDateTime.now().minusDays(20))
                            .build(),
                    KhachHang.builder()
                            .maKhachHang("KH05")
                            .hoTen("Vũ Thu Thảo")
                            .gioiTinh(false)
                            .ngaySinh(LocalDate.of(2003, 11, 15))
                            .email("thao.vu@gmail.com")
                            .soDienThoai("0965443322")
                            .matKhau("123456")
                            .trangThai(true)
                            .ngayTao(LocalDateTime.now().minusDays(10))
                            .build()
            );
            khachHangRepository.saveAll(newCustomers);
            log.info("Đã tạo thêm khách hàng mẫu cho cửa hàng.");
        }
    }

    private void seedOrders() {
        if (hoaDonRepository.count() >= 10) {
            return;
        }

        List<KhachHang> customers = khachHangRepository.findAll();
        List<NhanVien> employees = nhanVienRepository.findAll();
        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findAll();

        if (customers.isEmpty() || variants.isEmpty()) {
            return;
        }

        NhanVien defaultEmp = employees.isEmpty() ? null : employees.get(0);

        // Tạo 15 đơn hàng đa dạng trạng thái, ngày tháng và phương thức thanh toán
        Object[][] orderConfigs = {
                // {ma, khIdx, daysAgo, status, pttt, hinhThuc, ghiChu, varIdx1, q1, varIdx2, q2}
                {"HD-20261016-003", 0, 6, "Đã hoàn thành", true, true, "Giao giờ hành chính", 0, 2, 1, 1},
                {"HD-20261017-004", 1, 5, "Đã hoàn thành", false, true, "Gọi trước khi giao 15p", 2, 1, 4, 1},
                {"HD-20261017-005", 2, 5, "Đã hoàn thành", true, false, "Khách mua tại quầy", 3, 2, 0, 0},
                {"HD-20261018-006", 3, 4, "Đã hoàn thành", false, true, "Để bảo vệ nhận hộ", 5, 1, 6, 1},
                {"HD-20261019-007", 4, 3, "Đã hoàn thành", true, true, "Đóng gói cẩn thận giúp shop", 1, 3, 0, 0},
                {"HD-20261019-008", 0, 3, "Đã hoàn thành", false, false, "Khách ghé shop lấy", 4, 2, 2, 1},
                {"HD-20261020-009", 1, 2, "Đã hoàn thành", true, true, "Áo đẹp ủng hộ tiếp", 0, 1, 5, 2},
                {"HD-20261020-010", 2, 2, "Đang giao", false, true, "Ship gấp trong ngày", 3, 2, 1, 1},
                {"HD-20261021-011", 3, 1, "Đang giao", true, true, "Thanh toán VNPAY thành công", 6, 1, 4, 1},
                {"HD-20261021-012", 4, 1, "Đã hoàn thành", true, true, "Giao tầng 12 chung cư", 2, 2, 0, 0},
                {"HD-20261021-013", 0, 1, "Chờ xác nhận", false, true, "Chờ shop tư vấn size", 1, 1, 5, 1},
                {"HD-20261022-014", 1, 0, "Chờ xác nhận", true, true, "Đơn vừa đặt trưa nay", 0, 2, 3, 1},
                {"HD-20261022-015", 2, 0, "Chờ xác nhận", false, true, "Giao chiều nay", 4, 1, 6, 1},
                {"HD-20261018-016", 3, 4, "Đã hủy", false, true, "Khách đổi ý muốn mẫu khác", 2, 1, 0, 0},
                {"HD-20261019-017", 4, 3, "Đã hoàn thành", true, true, "Áo rất vừa vặn và thoáng", 5, 2, 1, 1}
        };

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < orderConfigs.length; i++) {
            Object[] cfg = orderConfigs[i];
            String ma = (String) cfg[0];
            int khIdx = (int) cfg[1] % customers.size();
            int daysAgo = (int) cfg[2];
            String status = (String) cfg[3];
            boolean pttt = (boolean) cfg[4];
            boolean hinhThuc = (boolean) cfg[5];
            String ghiChu = (String) cfg[6];
            int varIdx1 = (int) cfg[7] % variants.size();
            int q1 = (int) cfg[8];
            int varIdx2 = (int) cfg[9] % variants.size();
            int q2 = (int) cfg[10];

            LocalDateTime orderTime = now.minusDays(daysAgo).minusHours((i * 3) % 12).minusMinutes(i * 7);

            SanPhamChiTiet spct1 = variants.get(varIdx1);
            BigDecimal tienTruocGiam = spct1.getGiaBan().multiply(BigDecimal.valueOf(q1));

            List<HoaDonChiTiet> details = new ArrayList<>();
            HoaDonChiTiet ct1 = HoaDonChiTiet.builder()
                    .maHoaDonChiTiet("HDCT-" + ma + "-01")
                    .sanPhamChiTiet(spct1)
                    .soLuong(q1)
                    .donGia(spct1.getGiaBan())
                    .build();
            details.add(ct1);

            if (q2 > 0) {
                SanPhamChiTiet spct2 = variants.get(varIdx2);
                tienTruocGiam = tienTruocGiam.add(spct2.getGiaBan().multiply(BigDecimal.valueOf(q2)));
                HoaDonChiTiet ct2 = HoaDonChiTiet.builder()
                        .maHoaDonChiTiet("HDCT-" + ma + "-02")
                        .sanPhamChiTiet(spct2)
                        .soLuong(q2)
                        .donGia(spct2.getGiaBan())
                        .build();
                details.add(ct2);
            }

            BigDecimal phiGiaoHang = hinhThuc ? BigDecimal.valueOf(30000) : BigDecimal.ZERO;
            BigDecimal tongTien = tienTruocGiam.add(phiGiaoHang);

            HoaDon hd = HoaDon.builder()
                    .maHoaDon(ma)
                    .khachHang(customers.get(khIdx))
                    .nhanVien(defaultEmp)
                    .tienTruocGiam(tienTruocGiam)
                    .phiGiaoHang(phiGiaoHang)
                    .tongTien(tongTien)
                    .phuongThucThanhToan(pttt)
                    .hinhThucMuaHang(hinhThuc)
                    .diaChiGiaoHang(hinhThuc ? "Số " + (10 + i * 5) + " Đường Cầu Giấy, Hà Nội" : "Mua trực tiếp tại Shop")
                    .ghiChu(ghiChu)
                    .trangThai(status)
                    .ngayTao(orderTime)
                    .build();

            HoaDon savedHd = hoaDonRepository.save(hd);
            for (HoaDonChiTiet ct : details) {
                ct.setHoaDon(savedHd);
                hoaDonChiTietRepository.save(ct);
            }
        }
        log.info("Đã tạo 15 đơn hàng mẫu phong phú phục vụ hiển thị biểu đồ và thống kê Dashboard.");
    }

    private void adjustLowStockVariants() {
        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findAll();
        if (variants.size() >= 3) {
            // Đặt 3 biến thể có số lượng tồn thấp (3, 5, 7) để mục Cảnh báo tồn kho hoạt động thực tế
            variants.get(0).setSoLuongTon(4);
            variants.get(1).setSoLuongTon(2);
            if (variants.size() > 4) {
                variants.get(4).setSoLuongTon(6);
            }
            sanPhamChiTietRepository.saveAll(variants);
            log.info("Đã cập nhật cảnh báo tồn kho thấp cho 3 biến thể áo.");
        }
    }
}
