package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.dto.AuthUser;
import jakarta.servlet.http.HttpSession;

public interface AuthService {
    AuthUser login(String usernameOrAccount, String password);
    AuthUser getCurrentUser(HttpSession session);
    void setCurrentUser(HttpSession session, AuthUser user);
    void logout(HttpSession session);
}
