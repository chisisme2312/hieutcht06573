package com.luonvuituoi.shop.controller.client;

import com.luonvuituoi.shop.dto.AuthUser;
import com.luonvuituoi.shop.dto.CartDto;
import com.luonvuituoi.shop.dto.CartItemDto;
import com.luonvuituoi.shop.entity.GiamGia;
import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.entity.HoaDonChiTiet;
import com.luonvuituoi.shop.entity.KhachHang;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.repository.GiamGiaRepository;
import com.luonvuituoi.shop.repository.HoaDonChiTietRepository;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.repository.SanPhamChiTietRepository;
import com.luonvuituoi.shop.service.GioHangService;
import com.luonvuituoi.shop.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final GioHangService gioHangService;
    private final KhachHangRepository khachHangRepository;
    private final GiamGiaRepository giamGiaRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    // 1. Giao diện Thanh toán & Chọn thông tin người nhận
    @GetMapping({"/dat-hang", "/checkout"})
    public String trangThanhToan(HttpSession session, Model model, RedirectAttributes ra) {
        CartDto cart = gioHangService.getGioHang(session);
        if (cart == null || cart.getItems().isEmpty()) {
            ra.addFlashAttribute("thongBaoLoi", "Giỏ hàng của bạn đang trống! Vui lòng chọn sản phẩm trước khi thanh toán.");
            return "redirect:/gio-hang";
        }

        AuthUser currentUser = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        model.addAttribute("cart", cart);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("voucherList", giamGiaRepository.findByTrangThaiTrue());
        return "client/checkout";
    }

    // 2. Xử lý Đặt hàng Online (Trừ tồn kho nguyên tử, Chống bán trùng khi đông khách, Lưu hóa đơn & Chi tiết)
    @PostMapping("/dat-hang/xac-nhan")
    @Transactional
    public String xacNhanDatHang(
            @RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam("diaChi") String diaChi,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            @RequestParam(value = "phuongThucThanhToan", defaultValue = "COD") String phuongThucThanhToan,
            @RequestParam(value = "maVoucher", required = false) String maVoucher,
            HttpSession session,
            RedirectAttributes ra) {

        CartDto cart = gioHangService.getGioHang(session);
        if (cart == null || cart.getItems().isEmpty()) {
            ra.addFlashAttribute("thongBaoLoi", "Giỏ hàng trống! Không thể hoàn tất đặt hàng.");
            return "redirect:/gio-hang";
        }

        if (hoTen == null || hoTen.trim().isEmpty() || soDienThoai == null || soDienThoai.trim().isEmpty() || diaChi == null || diaChi.trim().isEmpty()) {
            ra.addFlashAttribute("thongBaoLoi", "Vui lòng nhập đầy đủ: Tên người nhận, Số điện thoại và Địa chỉ nhận hàng!");
            return "redirect:/dat-hang";
        }

        // BƯỚC 1: Kiểm tra và trừ tồn kho nguyên tử (Atomic deduction) cho tất cả sản phẩm
        // Nếu có 2 khách cùng mua 50 cái áo cùng lúc, query atomic sẽ chặn người thứ hai và rollback an toàn
        for (CartItemDto item : cart.getItems()) {
            int updated = sanPhamChiTietRepository.truTonKhoAtomic(item.getSanPhamChiTietId(), item.getSoLuong());
            if (updated <= 0) {
                // Hết hàng hoặc không đủ số lượng tồn
                var currentOpt = sanPhamChiTietRepository.findById(item.getSanPhamChiTietId());
                int tonHienTai = currentOpt.map(SanPhamChiTiet::getSoLuongTon).orElse(0);
                String tenSp = item.getTenSanPham() + " (" + item.getTenMauSac() + " - " + item.getTenKichThuoc() + ")";
                ra.addFlashAttribute("thongBaoLoi", "Sản phẩm [" + tenSp + "] không đủ số lượng tồn kho (chỉ còn " + tonHienTai + " cái). Vui lòng cập nhật lại giỏ hàng!");
                return "redirect:/gio-hang";
            }
            // Tự động chuyển ngừng hoạt động nếu tồn kho về 0
            sanPhamChiTietRepository.capNhatHetHang(item.getSanPhamChiTietId());
        }

        // BƯỚC 2: Xác định hoặc tạo Khách hàng
        AuthUser currentUser = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        KhachHang khachHang = null;
        if (currentUser != null && currentUser.isCustomer()) {
            khachHang = khachHangRepository.findById(currentUser.getId()).orElse(null);
        }
        if (khachHang == null) {
            String sdtTrim = soDienThoai.trim();
            khachHang = khachHangRepository.findBySoDienThoai(sdtTrim).orElse(null);
            if (khachHang == null) {
                khachHang = new KhachHang();
                khachHang.setMaKhachHang("KH" + System.currentTimeMillis() % 1000000);
                khachHang.setHoTen(hoTen.trim());
                khachHang.setSoDienThoai(sdtTrim);
                khachHang.setEmail(email != null && !email.isBlank() ? email.trim() : null);
                khachHang.setTrangThai(true);
                khachHang.setNgayTao(LocalDateTime.now());
                khachHang = khachHangRepository.save(khachHang);
            }
        }

        // BƯỚC 3: Tính toán Voucher giảm giá
        BigDecimal tamTinh = cart.getTamTinh();
        BigDecimal phiShip = cart.getPhiVanChuyen();
        BigDecimal tienGiam = BigDecimal.ZERO;
        GiamGia voucher = null;

        if (maVoucher != null && !maVoucher.isBlank()) {
            var ggOpt = giamGiaRepository.findByMaGiamGiaAndTrangThaiTrue(maVoucher.trim());
            if (ggOpt.isPresent()) {
                GiamGia gg = ggOpt.get();
                LocalDateTime now = LocalDateTime.now();
                if (!now.isBefore(gg.getNgayBatDau()) && !now.isAfter(gg.getNgayKetThuc()) && gg.getSoLuong() != null && gg.getSoLuong() > 0) {
                    if (gg.getGiaTriToiThieu() == null || tamTinh.compareTo(gg.getGiaTriToiThieu()) >= 0) {
                        if (gg.getLoaiGiamGia() != null && gg.getLoaiGiamGia() == 1) {
                            tienGiam = tamTinh.multiply(gg.getGiaTriGiam()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        } else {
                            tienGiam = gg.getGiaTriGiam();
                        }
                        if (gg.getToiDaGiamGia() != null && gg.getToiDaGiamGia().compareTo(BigDecimal.ZERO) > 0 && tienGiam.compareTo(gg.getToiDaGiamGia()) > 0) {
                            tienGiam = gg.getToiDaGiamGia();
                        }
                        if (tienGiam.compareTo(tamTinh) > 0) tienGiam = tamTinh;
                        voucher = gg;
                        // Trừ 1 lượt voucher
                        gg.setSoLuong(gg.getSoLuong() - 1);
                        giamGiaRepository.save(gg);
                    }
                }
            }
        }

        BigDecimal tongThanhToan = tamTinh.add(phiShip).subtract(tienGiam).max(BigDecimal.ZERO);

        // BƯỚC 4: Tạo và lưu Hóa đơn
        String maHd = "ONL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 3).toUpperCase(Locale.ROOT);
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHd);
        hd.setKhachHang(khachHang);
        hd.setGiamGia(voucher);
        hd.setTienTruocGiam(tamTinh);
        hd.setPhiGiaoHang(phiShip);
        hd.setTongTien(tongThanhToan);
        hd.setPhuongThucThanhToan("BANKING".equalsIgnoreCase(phuongThucThanhToan)); // true = Chuyển khoản, false = COD
        hd.setHinhThucMuaHang(false); // false = Mua Online
        hd.setDiaChiGiaoHang("[Người nhận: " + hoTen.trim() + " - SĐT: " + soDienThoai.trim() + "] " + diaChi.trim());
        hd.setGhiChu(ghiChu != null && !ghiChu.isBlank() ? ghiChu.trim() : null);
        hd.setTrangThai("Chờ xác nhận");
        hd.setNgayTao(LocalDateTime.now());
        HoaDon savedHd = hoaDonRepository.save(hd);

        // BƯỚC 5: Lưu danh sách Hóa Đơn Chi Tiết
        for (CartItemDto item : cart.getItems()) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getSanPhamChiTietId()).orElse(null);
            if (spct != null) {
                HoaDonChiTiet ct = new HoaDonChiTiet();
                ct.setHoaDon(savedHd);
                ct.setSanPhamChiTiet(spct);
                ct.setMaHoaDonChiTiet(savedHd.getMaHoaDon() + "-" + spct.getId());
                ct.setDonGia(item.getGiaBan());
                ct.setSoLuong(item.getSoLuong());
                hoaDonChiTietRepository.save(ct);
            }
        }

        // BƯỚC 6: Xóa sạch giỏ hàng trong session
        gioHangService.xoaTatCa(session);

        ra.addFlashAttribute("thongBaoThanhCong", "Chúc mừng! Đơn hàng " + savedHd.getMaHoaDon() + " đã được đặt thành công!");
        return "redirect:/dat-hang/thanh-cong/" + savedHd.getId();
    }

    // 3. Trang thông báo Đặt Hàng Thành Công chi tiết
    @GetMapping("/dat-hang/thanh-cong/{id}")
    public String datHangThanhCong(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        var hdOpt = hoaDonRepository.findById(id);
        if (hdOpt.isEmpty()) {
            ra.addFlashAttribute("thongBaoLoi", "Không tìm thấy hóa đơn đơn hàng!");
            return "redirect:/";
        }

        HoaDon hd = hdOpt.get();
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonId(id);
        model.addAttribute("hoaDon", hd);
        model.addAttribute("chiTietList", chiTietList);
        return "client/thanh-cong";
    }

    // 4. Lịch sử đơn hàng của tôi
    @GetMapping({"/don-hang-cua-toi", "/dhangcuatoi"})
    public String donHangCuaToi(HttpSession session, Model model) {
        AuthUser currentUser = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        List<HoaDon> danhSachDonHang;
        if (currentUser != null) {
            danhSachDonHang = hoaDonRepository.findByKhachHangIdOrderByNgayTaoDesc(currentUser.getId());
        } else {
            // Khách chưa đăng nhập: lấy danh sách đơn online mới nhất
            danhSachDonHang = hoaDonRepository.findByHinhThucMuaHangFalseOrderByNgayTaoDesc().stream().limit(10).toList();
        }
        model.addAttribute("danhSachDonHang", danhSachDonHang);
        model.addAttribute("currentUser", currentUser);
        return "client/dhangcuatoi";
    }
}
