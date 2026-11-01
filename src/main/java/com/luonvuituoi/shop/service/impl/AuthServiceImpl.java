package com.luonvuituoi.shop.service.impl;

import com.luonvuituoi.shop.dto.AuthUser;
import com.luonvuituoi.shop.entity.KhachHang;
import com.luonvuituoi.shop.entity.NhanVien;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.repository.NhanVienRepository;
import com.luonvuituoi.shop.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    public static final String SESSION_USER_KEY = "currentUser";

    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;

    @Override
    public AuthUser login(String usernameOrAccount, String password) {
        if (usernameOrAccount == null || usernameOrAccount.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập tên đăng nhập hoặc email!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu!");
        }

        String input = usernameOrAccount.trim();
        String pass = password.trim();

        // 1. Kiểm tra tài khoản Nhân viên / Quản trị viên
        Optional<NhanVien> nvOpt = nhanVienRepository.findByTenDangNhap(input);
        if (nvOpt.isEmpty()) {
            nvOpt = nhanVienRepository.findByEmail(input);
        }

        if (nvOpt.isPresent()) {
            NhanVien nv = nvOpt.get();
            if (Boolean.FALSE.equals(nv.getTrangThai())) {
                throw new IllegalArgumentException("Tài khoản nhân viên này đang bị tạm khóa!");
            }
            if (!pass.equals(nv.getMatKhau())) {
                throw new IllegalArgumentException("Mật khẩu không chính xác!");
            }

            String roleName = (nv.getVaiTro() != null && nv.getVaiTro().getTenVaiTro() != null)
                    ? nv.getVaiTro().getTenVaiTro().toUpperCase()
                    : "STAFF";

            return AuthUser.builder()
                    .id(nv.getId())
                    .maNguoiDung(nv.getMaNhanVien())
                    .hoTen(nv.getHoVaTen())
                    .tenDangNhap(nv.getTenDangNhap())
                    .email(nv.getEmail())
                    .soDienThoai(nv.getSoDienThoai())
                    .vaiTro(roleName)
                    .build();
        }

        // 2. Kiểm tra tài khoản Khách hàng mua online
        Optional<KhachHang> khOpt = khachHangRepository.findByEmail(input);
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepository.findBySoDienThoai(input);
        }

        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            if (Boolean.FALSE.equals(kh.getTrangThai())) {
                throw new IllegalArgumentException("Tài khoản khách hàng này đang bị khóa!");
            }
            if (!pass.equals(kh.getMatKhau())) {
                throw new IllegalArgumentException("Mật khẩu không chính xác!");
            }

            return AuthUser.builder()
                    .id(kh.getId())
                    .maNguoiDung(kh.getMaKhachHang())
                    .hoTen(kh.getHoTen())
                    .tenDangNhap(kh.getEmail() != null ? kh.getEmail() : kh.getSoDienThoai())
                    .email(kh.getEmail())
                    .soDienThoai(kh.getSoDienThoai())
                    .vaiTro("CUSTOMER")
                    .build();
        }

        throw new IllegalArgumentException("Tài khoản không tồn tại trên hệ thống!");
    }

    @Override
    public AuthUser getCurrentUser(HttpSession session) {
        if (session == null) return null;
        return (AuthUser) session.getAttribute(SESSION_USER_KEY);
    }

    @Override
    public void setCurrentUser(HttpSession session, AuthUser user) {
        if (session != null) {
            session.setAttribute(SESSION_USER_KEY, user);
        }
    }

    @Override
    public void logout(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_USER_KEY);
        }
    }
}
