package com.luonvuituoi.shop.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String maNguoiDung;
    private String hoTen;
    private String tenDangNhap;
    private String email;
    private String soDienThoai;
    private String vaiTro; // "ADMIN", "STAFF", "CUSTOMER"

    public boolean isNhanVien() {
        return "ADMIN".equalsIgnoreCase(vaiTro) || "STAFF".equalsIgnoreCase(vaiTro);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(vaiTro);
    }

    public boolean isStaff() {
        return "STAFF".equalsIgnoreCase(vaiTro);
    }

    public boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(vaiTro);
    }
}
