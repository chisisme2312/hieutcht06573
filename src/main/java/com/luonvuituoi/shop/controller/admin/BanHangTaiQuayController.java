package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.dto.AuthUser;
import com.luonvuituoi.shop.entity.*;
import com.luonvuituoi.shop.repository.*;
import com.luonvuituoi.shop.service.AuthService;
import com.luonvuituoi.shop.service.BanHangService;
import com.luonvuituoi.shop.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/ban-hang")
@RequiredArgsConstructor
public class BanHangTaiQuayController {
    private final BanHangService banHangService;
    private final HoaDonRepository hoaDonRepository;
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final GiamGiaRepository giamGiaRepository;

    @Value("${pos.qr.bank-id:}") private String bankId;
    @Value("${pos.qr.account-number:}") private String accountNumber;
    @Value("${pos.qr.account-name:}") private String accountName;

    @GetMapping
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String index(@RequestParam(required = false) Integer hoaDonId, HttpSession session, Model model, RedirectAttributes ra) {
        AuthUser auth = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (auth == null || !auth.isNhanVien()) {
            ra.addFlashAttribute("errorMsg", "Vui lòng đăng nhập tài khoản nhân viên để truy cập Bán hàng tại quầy!");
            return "redirect:/login?redirect=/admin/ban-hang";
        }

        // Mỗi tài khoản chỉ quản lý hóa đơn của chính mình, tuyệt đối không chung đụng
        final Integer currentNvId = auth.getId();
        List<HoaDon> hoaDonCho = hoaDonRepository.findByHinhThucMuaHangTrueOrderByNgayTaoDesc().stream()
                .filter(h -> BanHangService.CHO_THANH_TOAN.equals(h.getTrangThai()))
                .filter(h -> h.getNhanVien() != null && h.getNhanVien().getId().equals(currentNvId))
                .toList();

        HoaDon selected = null;
        if (hoaDonId != null) {
            selected = hoaDonRepository.findById(hoaDonId).orElse(null);
            // Chỉ xem được hóa đơn của chính tài khoản mình
            if (selected != null && (selected.getNhanVien() == null || !selected.getNhanVien().getId().equals(currentNvId))) {
                selected = null;
            }
        }
        if (selected != null && (!Boolean.TRUE.equals(selected.getHinhThucMuaHang()) || !BanHangService.CHO_THANH_TOAN.equals(selected.getTrangThai()))) selected = null;
        if (selected == null && !hoaDonCho.isEmpty()) selected = hoaDonCho.get(0);
        model.addAttribute("hoaDonCho", hoaDonCho);
        model.addAttribute("hoaDonDangChon", selected);
        model.addAttribute("chiTietHoaDon", selected == null ? List.of() : banHangService.layChiTiet(selected.getId()));
        model.addAttribute("sanPhamList", sanPhamRepository.findAllActiveWithDetails());
        model.addAttribute("khachHangList", khachHangRepository.findAll());
        model.addAttribute("nhanVienList", nhanVienRepository.findByTrangThaiTrue());
        model.addAttribute("voucherList", giamGiaRepository.findByTrangThaiTrue());
        model.addAttribute("currentUser", auth);
        return "admin/ban-hang/index";
    }

    @PostMapping("/tao-hoa-don")
    public String taoHoaDon(HttpSession session, RedirectAttributes ra) {
        AuthUser auth = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (auth == null || !auth.isNhanVien()) {
            ra.addFlashAttribute("errorMsg", "Vui lòng đăng nhập!");
            return "redirect:/login?redirect=/admin/ban-hang";
        }

        // Kiểm tra nguyên tắc: Mỗi tài khoản chỉ có tối đa 1 hóa đơn chờ tại quầy
        final Integer currentNvId = auth.getId();
        List<HoaDon> existingCho = hoaDonRepository.findByHinhThucMuaHangTrueOrderByNgayTaoDesc().stream()
                .filter(h -> BanHangService.CHO_THANH_TOAN.equals(h.getTrangThai()))
                .filter(h -> h.getNhanVien() != null && h.getNhanVien().getId().equals(currentNvId))
                .toList();

        if (!existingCho.isEmpty()) {
            HoaDon hdTonTai = existingCho.get(0);
            ra.addFlashAttribute("error", "Mỗi tài khoản chỉ được mở 1 hóa đơn chờ tại quầy! Bạn đang có hóa đơn [" + hdTonTai.getMaHoaDon() + "], vui lòng hoàn tất hoặc hủy trước khi tạo mới.");
            return "redirect:/admin/ban-hang?hoaDonId=" + hdTonTai.getId();
        }

        try {
            HoaDon hd = banHangService.taoHoaDon(currentNvId);
            ra.addFlashAttribute("success", "Đã tạo hóa đơn mới: " + hd.getMaHoaDon());
            return "redirect:/admin/ban-hang?hoaDonId=" + hd.getId();
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/ban-hang";
        }
    }

    @PostMapping("/{id}/them-san-pham")
    public String themSanPham(@PathVariable Integer id, @RequestParam Integer sanPhamChiTietId, @RequestParam(defaultValue = "1") Integer soLuong, HttpSession session, RedirectAttributes ra) {
        return xuLy(id, session, ra, () -> banHangService.themSanPham(id, sanPhamChiTietId, soLuong));
    }

    @PostMapping("/{id}/cap-nhat-so-luong")
    public String capNhatSoLuong(@PathVariable Integer id, @RequestParam Integer chiTietId, @RequestParam Integer soLuong, HttpSession session, RedirectAttributes ra) {
        return xuLy(id, session, ra, () -> banHangService.capNhatSoLuong(id, chiTietId, soLuong));
    }

    @PostMapping("/{id}/xoa-san-pham")
    public String xoaSanPham(@PathVariable Integer id, @RequestParam Integer chiTietId, HttpSession session, RedirectAttributes ra) {
        return xuLy(id, session, ra, () -> banHangService.xoaSanPham(id, chiTietId));
    }

    @PostMapping("/{id}/gan-khach-hang")
    public String ganKhachHang(@PathVariable Integer id, @RequestParam(required = false) Integer khachHangId, HttpSession session, RedirectAttributes ra) {
        return xuLy(id, session, ra, () -> banHangService.ganKhachHang(id, khachHangId));
    }

    @PostMapping("/{id}/them-khach-hang")
    public String themKhachHang(@PathVariable Integer id, @RequestParam String hoTen, @RequestParam(required = false) String soDienThoai, @RequestParam(required = false) String email, HttpSession session, RedirectAttributes ra) {
        return xuLy(id, session, ra, () -> {
            KhachHang kh = banHangService.themKhachHangNhanh(hoTen, soDienThoai, email);
            banHangService.ganKhachHang(id, kh.getId());
        });
    }

    @PostMapping("/{id}/voucher")
    public String apDungVoucher(@PathVariable Integer id, @RequestParam(required = false) String maVoucher, HttpSession session, RedirectAttributes ra) {
        return xuLy(id, session, ra, () -> banHangService.apDungVoucher(id, maVoucher));
    }

    @PostMapping("/{id}/thanh-toan-tien-mat")
    public String thanhToanTienMat(@PathVariable Integer id, @RequestParam BigDecimal tienKhachDua, HttpSession session, RedirectAttributes ra) {
        AuthUser auth = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (!kiemTraQuyenHoaDon(id, auth)) {
            ra.addFlashAttribute("error", "Bạn không có quyền thanh toán hóa đơn của nhân viên khác!");
            return "redirect:/admin/ban-hang";
        }
        try { 
            banHangService.thanhToanTienMat(id, tienKhachDua); 
            ra.addFlashAttribute("success", "Thanh toán tiền mặt thành công."); 
            return "redirect:/admin/hoa-don/" + id; 
        } catch (RuntimeException e) { 
            ra.addFlashAttribute("error", e.getMessage()); 
            return "redirect:/admin/ban-hang?hoaDonId=" + id; 
        }
    }

    @GetMapping("/{id}/qr")
    public String hienThiQR(@PathVariable Integer id, HttpSession session, Model model, RedirectAttributes ra) {
        AuthUser auth = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (!kiemTraQuyenHoaDon(id, auth)) {
            ra.addFlashAttribute("error", "Bạn không có quyền xem QR hóa đơn của nhân viên khác!");
            return "redirect:/admin/ban-hang";
        }
        HoaDon hd = hoaDonRepository.findById(id).orElse(null);
        if (hd == null || !BanHangService.CHO_THANH_TOAN.equals(hd.getTrangThai()) || !Boolean.TRUE.equals(hd.getHinhThucMuaHang())) {
            ra.addFlashAttribute("error", "Hóa đơn không hợp lệ hoặc đã thanh toán."); return "redirect:/admin/ban-hang";
        }
        List<HoaDonChiTiet> chiTiet = banHangService.layChiTiet(id);
        if (chiTiet.isEmpty() || hd.getTongTien() == null || hd.getTongTien().compareTo(BigDecimal.ZERO) <= 0) {
            ra.addFlashAttribute("error", "Hóa đơn chưa có sản phẩm hoặc tổng thanh toán bằng 0.");
            return "redirect:/admin/ban-hang?hoaDonId=" + id;
        }
        model.addAttribute("hoaDon", hd);
        model.addAttribute("chiTietHoaDon", chiTiet);
        boolean configured = bankId != null && !bankId.isBlank() && accountNumber != null && !accountNumber.isBlank();
        model.addAttribute("qrConfigured", configured);
        String qrUrl = "";
        if (configured) {
            String info = URLEncoder.encode("Thanh toan " + hd.getMaHoaDon(), StandardCharsets.UTF_8);
            String name = URLEncoder.encode(accountName == null ? "" : accountName, StandardCharsets.UTF_8);
            qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNumber + "-compact2.png?amount=" + hd.getTongTien().longValue() + "&addInfo=" + info + "&accountName=" + name;
        }
        model.addAttribute("qrUrl", qrUrl);
        return "admin/ban-hang/qr";
    }

    @PostMapping("/{id}/xac-nhan-qr")
    public String xacNhanQR(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        AuthUser auth = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (!kiemTraQuyenHoaDon(id, auth)) {
            ra.addFlashAttribute("error", "Bạn không có quyền xác nhận hóa đơn của nhân viên khác!");
            return "redirect:/admin/ban-hang";
        }
        try { 
            banHangService.xacNhanDaNhanChuyenKhoan(id); 
            ra.addFlashAttribute("success", "Đã xác nhận thanh toán chuyển khoản."); 
            return "redirect:/admin/hoa-don/" + id; 
        } catch (RuntimeException e) { 
            ra.addFlashAttribute("error", e.getMessage()); 
            return "redirect:/admin/ban-hang?hoaDonId=" + id; 
        }
    }

    @PostMapping("/{id}/huy")
    public String huy(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        return xuLy(id, session, ra, () -> banHangService.huyHoaDon(id));
    }

    private boolean kiemTraQuyenHoaDon(Integer hoaDonId, AuthUser auth) {
        if (auth == null || auth.getId() == null) return false;
        var hdOpt = hoaDonRepository.findById(hoaDonId);
        if (hdOpt.isEmpty()) return false;
        HoaDon hd = hdOpt.get();
        // Mỗi tài khoản chỉ quản lý hóa đơn của chính mình, tuyệt đối không chung đụng
        return hd.getNhanVien() != null && hd.getNhanVien().getId().equals(auth.getId());
    }

    private String xuLy(Integer id, HttpSession session, RedirectAttributes ra, Runnable action) {
        AuthUser auth = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (!kiemTraQuyenHoaDon(id, auth)) {
            ra.addFlashAttribute("error", "Bạn không có quyền thao tác trên hóa đơn chờ của nhân viên khác!");
            return "redirect:/admin/ban-hang";
        }
        try { 
            action.run(); 
            ra.addFlashAttribute("success", "Đã cập nhật hóa đơn."); 
        } catch (RuntimeException e) { 
            ra.addFlashAttribute("error", e.getMessage()); 
        }
        return "redirect:/admin/ban-hang?hoaDonId=" + id;
    }
}
