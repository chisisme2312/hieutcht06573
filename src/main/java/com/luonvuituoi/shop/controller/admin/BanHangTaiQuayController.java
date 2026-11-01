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
    public String index(@RequestParam(required = false) Integer hoaDonId, Model model) {
        List<HoaDon> hoaDonCho = hoaDonRepository.findByHinhThucMuaHangTrueOrderByNgayTaoDesc().stream()
                .filter(h -> BanHangService.CHO_THANH_TOAN.equals(h.getTrangThai())).toList();
        HoaDon selected = null;
        if (hoaDonId != null) selected = hoaDonRepository.findById(hoaDonId).orElse(null);
        if (selected != null && (!Boolean.TRUE.equals(selected.getHinhThucMuaHang()) || !BanHangService.CHO_THANH_TOAN.equals(selected.getTrangThai()))) selected = null;
        if (selected == null && !hoaDonCho.isEmpty()) selected = hoaDonCho.get(0);
        model.addAttribute("hoaDonCho", hoaDonCho);
        model.addAttribute("hoaDonDangChon", selected);
        model.addAttribute("chiTietHoaDon", selected == null ? List.of() : banHangService.layChiTiet(selected.getId()));
        model.addAttribute("sanPhamList", sanPhamRepository.findAllActiveWithDetails());
        model.addAttribute("khachHangList", khachHangRepository.findAll());
        model.addAttribute("nhanVienList", nhanVienRepository.findByTrangThaiTrue());
        model.addAttribute("voucherList", giamGiaRepository.findByTrangThaiTrue());
        return "admin/ban-hang/index";
    }

    @PostMapping("/tao-hoa-don")
    public String taoHoaDon(HttpSession session, RedirectAttributes ra) {
        try {
            Integer nvId = null;
            AuthUser auth = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
            if (auth != null && auth.isNhanVien()) {
                nvId = auth.getId();
            } else {
                var activeNvs = nhanVienRepository.findByTrangThaiTrue();
                if (!activeNvs.isEmpty()) {
                    nvId = activeNvs.get(0).getId();
                }
            }
            HoaDon hd = banHangService.taoHoaDon(nvId);
            ra.addFlashAttribute("success", "Đã tạo hóa đơn mới: " + hd.getMaHoaDon());
            return "redirect:/admin/ban-hang?hoaDonId=" + hd.getId();
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/ban-hang";
        }
    }

    @PostMapping("/{id}/them-san-pham")
    public String themSanPham(@PathVariable Integer id, @RequestParam Integer sanPhamChiTietId, @RequestParam(defaultValue = "1") Integer soLuong, RedirectAttributes ra) {
        return xuLy(id, ra, () -> banHangService.themSanPham(id, sanPhamChiTietId, soLuong));
    }

    @PostMapping("/{id}/cap-nhat-so-luong")
    public String capNhatSoLuong(@PathVariable Integer id, @RequestParam Integer chiTietId, @RequestParam Integer soLuong, RedirectAttributes ra) {
        return xuLy(id, ra, () -> banHangService.capNhatSoLuong(id, chiTietId, soLuong));
    }

    @PostMapping("/{id}/xoa-san-pham")
    public String xoaSanPham(@PathVariable Integer id, @RequestParam Integer chiTietId, RedirectAttributes ra) {
        return xuLy(id, ra, () -> banHangService.xoaSanPham(id, chiTietId));
    }

    @PostMapping("/{id}/gan-khach-hang")
    public String ganKhachHang(@PathVariable Integer id, @RequestParam(required = false) Integer khachHangId, RedirectAttributes ra) {
        return xuLy(id, ra, () -> banHangService.ganKhachHang(id, khachHangId));
    }

    @PostMapping("/{id}/them-khach-hang")
    public String themKhachHang(@PathVariable Integer id, @RequestParam String hoTen, @RequestParam(required = false) String soDienThoai, @RequestParam(required = false) String email, RedirectAttributes ra) {
        try { KhachHang kh = banHangService.themKhachHangNhanh(hoTen, soDienThoai, email); banHangService.ganKhachHang(id, kh.getId()); ra.addFlashAttribute("success", "Đã thêm và chọn khách hàng."); }
        catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/ban-hang?hoaDonId=" + id;
    }

    @PostMapping("/{id}/voucher")
    public String apDungVoucher(@PathVariable Integer id, @RequestParam(required = false) String maVoucher, RedirectAttributes ra) {
        return xuLy(id, ra, () -> banHangService.apDungVoucher(id, maVoucher));
    }

    @PostMapping("/{id}/thanh-toan-tien-mat")
    public String thanhToanTienMat(@PathVariable Integer id, @RequestParam BigDecimal tienKhachDua, RedirectAttributes ra) {
        try { banHangService.thanhToanTienMat(id, tienKhachDua); ra.addFlashAttribute("success", "Thanh toán tiền mặt thành công."); return "redirect:/admin/hoa-don/" + id; }
        catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); return "redirect:/admin/ban-hang?hoaDonId=" + id; }
    }

    @GetMapping("/{id}/qr")
    public String hienThiQR(@PathVariable Integer id, Model model, RedirectAttributes ra) {
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
    public String xacNhanQR(@PathVariable Integer id, RedirectAttributes ra) {
        try { banHangService.xacNhanDaNhanChuyenKhoan(id); ra.addFlashAttribute("success", "Đã xác nhận thanh toán chuyển khoản."); return "redirect:/admin/hoa-don/" + id; }
        catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); return "redirect:/admin/ban-hang?hoaDonId=" + id; }
    }

    @PostMapping("/{id}/huy")
    public String huy(@PathVariable Integer id, RedirectAttributes ra) {
        return xuLy(id, ra, () -> banHangService.huyHoaDon(id));
    }

    private String xuLy(Integer id, RedirectAttributes ra, Runnable action) {
        try { action.run(); ra.addFlashAttribute("success", "Đã cập nhật hóa đơn."); }
        catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/ban-hang?hoaDonId=" + id;
    }
}
