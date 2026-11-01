package com.luonvuituoi.shop.controller;

import com.luonvuituoi.shop.dto.AuthUser;
import com.luonvuituoi.shop.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public AuthUser populateCurrentUser(HttpSession session) {
        if (session == null) return null;
        return (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
    }
}
