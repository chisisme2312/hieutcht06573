package com.luonvuituoi.shop.config;

import com.luonvuituoi.shop.dto.AuthUser;
import com.luonvuituoi.shop.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AdminSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        AuthUser currentUser = (session != null) ? (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY) : null;

        // 1. Chưa đăng nhập
        if (currentUser == null) {
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            String fullUrl = (query != null && !query.isBlank()) ? (uri + "?" + query) : uri;
            String encoded = URLEncoder.encode(fullUrl, StandardCharsets.UTF_8);
            response.sendRedirect("/login?redirect=" + encoded + "&error=login_required");
            return false;
        }

        // 2. Đã đăng nhập nhưng là Khách hàng (không có quyền vào trang quản trị)
        if (!currentUser.isNhanVien()) {
            response.sendRedirect("/?error=access_denied");
            return false;
        }

        // 3. Là Admin hoặc Nhân viên -> Cho phép truy cập
        return true;
    }
}
