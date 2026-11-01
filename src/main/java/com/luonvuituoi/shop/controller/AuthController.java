package com.luonvuituoi.shop.controller;

import com.luonvuituoi.shop.dto.AuthUser;
import com.luonvuituoi.shop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
            return "redirect:/";
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

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        authService.logout(session);
        ra.addFlashAttribute("successMsg", "Bạn đã đăng xuất thành công.");
        return "redirect:/login?logout=success";
    }
}
