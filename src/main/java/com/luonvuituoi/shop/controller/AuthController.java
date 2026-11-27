package com.luonvuituoi.shop.controller;

import com.luonvuituoi.shop.dto.AuthUser;
import com.luonvuituoi.shop.entity.DiaChi;
import com.luonvuituoi.shop.entity.KhachHang;
import com.luonvuituoi.shop.repository.Dcrepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KhachHangRepository khachHangRepository;
    private final Dcrepository dcrepository;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect,
                            @RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            HttpSession session,
                            Model model) {
        AuthUser currentUser = authService.getCurrentUser(session);
        if (currentUser != null) {
            if (currentUser.isNhanVien()) {
                return (redirect != null && redirect.startsWith("/admin")) ? "redirect:" + redirect : "redirect:/admin";
            }
            return (redirect != null && !redirect.isBlank() && !redirect.startsWith("/admin")) ? "redirect:" + redirect : "redirect:/";
        }

        if ("login_required".equals(error)) {
            model.addAttribute("errorMsg", "Vui lòng đăng nhập tài khoản quản trị để truy cập chức năng này!");
        } else if ("access_denied".equals(error)) {
            model.addAttribute("errorMsg", "Tài khoản khách hàng không có quyền truy cập trang quản trị!");
        }

        if ("success".equals(logout)) {
            model.addAttribute("successMsg", "Đã đăng xuất thành công khỏi hệ thống!");
        }

        model.addAttribute("redirect", redirect);
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("usernameOrAccount") String usernameOrAccount,
                          @RequestParam("password") String password,
                          @RequestParam(value = "redirect", required = false) String redirect,
                          HttpSession session,
                          Model model,
                          RedirectAttributes ra) {
        try {
            AuthUser user = authService.login(usernameOrAccount, password);
            authService.setCurrentUser(session, user);

            if (user.isNhanVien()) {
                ra.addFlashAttribute("thongBao", "Xin chào " + user.getHoTen() + " (" + user.getVaiTro() + ") đã đăng nhập!");
                if (redirect != null && !redirect.isBlank() && redirect.startsWith("/admin")) {
                    return "redirect:" + redirect;
                }
                return "redirect:/admin";
            } else {
                ra.addFlashAttribute("thongBao", "Xin chào " + user.getHoTen() + "!");
                if (redirect != null && !redirect.isBlank() && !redirect.startsWith("/admin")) {
                    return "redirect:" + redirect;
                }
                return "redirect:/";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("usernameOrAccount", usernameOrAccount);
            model.addAttribute("redirect", redirect);
            return "auth/login";
        }
    }

    // 1. Màn hình Đăng Ký Tài Khoản Khách Hàng
    @GetMapping({"/dang-ky", "/register"})
    public String registerPage(@RequestParam(value = "redirect", required = false) String redirect,
                               HttpSession session,
                               Model model) {
        AuthUser currentUser = authService.getCurrentUser(session);
        if (currentUser != null) {
            return (redirect != null && !redirect.isBlank() && !redirect.startsWith("/admin")) ? "redirect:" + redirect : "redirect:/";
        }
        model.addAttribute("redirect", redirect);
        return "auth/register";
    }

    // 2. Xử lý Đăng Ký Tài Khoản Khách Hàng
    @PostMapping({"/dang-ky", "/register"})
    @Transactional
    public String doRegister(
            @RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(value = "diaChi", required = false) String diaChi,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpSession session,
            Model model,
            RedirectAttributes ra) {

        if (hoTen == null || hoTen.trim().isEmpty()) {
            model.addAttribute("errorMsg", "Vui lòng nhập họ và tên của bạn!");
            model.addAttribute("redirect", redirect);
            return "auth/register";
        }

        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            model.addAttribute("errorMsg", "Vui lòng nhập số điện thoại!");
            model.addAttribute("hoTen", hoTen);
            model.addAttribute("email", email);
            model.addAttribute("diaChi", diaChi);
            model.addAttribute("redirect", redirect);
            return "auth/register";
        }

        String sdtTrim = soDienThoai.trim();
        String emailTrim = (email != null && !email.isBlank()) ? email.trim() : null;

        var khTonTaiOpt = khachHangRepository.findBySoDienThoai(sdtTrim);
        if (khTonTaiOpt.isPresent()) {
            // Số điện thoại đã có sẵn: Cập nhật thông tin mới nhất và tự động đăng nhập, chuyển ngay về giao diện chính
            KhachHang kh = khTonTaiOpt.get();
            if (hoTen != null && !hoTen.isBlank()) kh.setHoTen(hoTen.trim());
            if (emailTrim != null) kh.setEmail(emailTrim);
            if (password != null && !password.isBlank() && password.equals(confirmPassword)) {
                kh.setMatKhau(password.trim());
            }
            khachHangRepository.save(kh);

            if (diaChi != null && !diaChi.isBlank()) {
                var dsDc = dcrepository.findByKhachHangId(kh.getId());
                DiaChi dc = dsDc.isEmpty() ? new DiaChi() : dsDc.get(0);
                dc.setKhachHang(kh);
                dc.setDuong(diaChi.trim());
                dc.setMacDinh(true);
                dcrepository.save(dc);
            }

            AuthUser authUser = AuthUser.builder()
                    .id(kh.getId())
                    .maNguoiDung(kh.getMaKhachHang())
                    .hoTen(kh.getHoTen())
                    .tenDangNhap(kh.getEmail() != null ? kh.getEmail() : kh.getSoDienThoai())
                    .email(kh.getEmail())
                    .soDienThoai(kh.getSoDienThoai())
                    .vaiTro("CUSTOMER")
                    .build();
            authService.setCurrentUser(session, authUser);

            ra.addFlashAttribute("thongBaoThanhCong", "Tài khoản [" + sdtTrim + "] đã sẵn sàng! Chào mừng " + kh.getHoTen() + " quay trở lại mua sắm.");
            return "redirect:/";
        }

        if (emailTrim != null) {
            var khEmailOpt = khachHangRepository.findByEmail(emailTrim);
            if (khEmailOpt.isPresent()) {
                // Email đã có sẵn: Tự động đăng nhập vào tài khoản đó luôn và về trang chủ
                KhachHang kh = khEmailOpt.get();
                if (hoTen != null && !hoTen.isBlank()) kh.setHoTen(hoTen.trim());
                if (password != null && !password.isBlank() && password.equals(confirmPassword)) {
                    kh.setMatKhau(password.trim());
                }
                khachHangRepository.save(kh);

                AuthUser authUser = AuthUser.builder()
                        .id(kh.getId())
                        .maNguoiDung(kh.getMaKhachHang())
                        .hoTen(kh.getHoTen())
                        .tenDangNhap(kh.getEmail())
                        .email(kh.getEmail())
                        .soDienThoai(kh.getSoDienThoai())
                        .vaiTro("CUSTOMER")
                        .build();
                authService.setCurrentUser(session, authUser);

                ra.addFlashAttribute("thongBaoThanhCong", "Tài khoản [" + emailTrim + "] đã sẵn sàng! Chào mừng " + kh.getHoTen() + " quay trở lại.");
                return "redirect:/";
            }
        }

        if (password == null || password.trim().length() < 4) {
            model.addAttribute("errorMsg", "Mật khẩu phải có ít nhất 4 ký tự!");
            model.addAttribute("hoTen", hoTen);
            model.addAttribute("soDienThoai", soDienThoai);
            model.addAttribute("email", email);
            model.addAttribute("diaChi", diaChi);
            model.addAttribute("redirect", redirect);
            return "auth/register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMsg", "Xác nhận mật khẩu không khớp!");
            model.addAttribute("hoTen", hoTen);
            model.addAttribute("soDienThoai", soDienThoai);
            model.addAttribute("email", email);
            model.addAttribute("diaChi", diaChi);
            model.addAttribute("redirect", redirect);
            return "auth/register";
        }

        // Tạo khách hàng mới
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang("KH" + System.currentTimeMillis() % 1000000);
        kh.setHoTen(hoTen.trim());
        kh.setSoDienThoai(sdtTrim);
        kh.setEmail(emailTrim);
        kh.setMatKhau(password.trim());
        kh.setGioiTinh(true);
        kh.setTrangThai(true);
        kh.setNgayTao(LocalDateTime.now());
        kh = khachHangRepository.save(kh);

        // Lưu địa chỉ mặc định nếu có
        if (diaChi != null && !diaChi.isBlank()) {
            DiaChi dc = new DiaChi();
            dc.setKhachHang(kh);
            dc.setDuong(diaChi.trim());
            dc.setMacDinh(true);
            dcrepository.save(dc);
        }

        // Tự động đăng nhập luôn cho khách hàng
        AuthUser authUser = AuthUser.builder()
                .id(kh.getId())
                .maNguoiDung(kh.getMaKhachHang())
                .hoTen(kh.getHoTen())
                .tenDangNhap(kh.getEmail() != null ? kh.getEmail() : kh.getSoDienThoai())
                .email(kh.getEmail())
                .soDienThoai(kh.getSoDienThoai())
                .vaiTro("CUSTOMER")
                .build();
        authService.setCurrentUser(session, authUser);

        ra.addFlashAttribute("thongBaoThanhCong", "Đăng ký tài khoản thành công! Chào mừng " + kh.getHoTen() + " đến với Luôn Vui Tươi Shop.");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        authService.logout(session);
        ra.addFlashAttribute("successMsg", "Bạn đã đăng xuất thành công.");
        return "redirect:/login?logout=success";
    }
}
