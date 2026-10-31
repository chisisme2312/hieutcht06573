package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.dto.CartDto;
import jakarta.servlet.http.HttpSession;

public interface GioHangService {
    CartDto getGioHang(HttpSession session);
    CartDto themSanPham(HttpSession session, Integer sanPhamChiTietId, Integer soLuong);
    CartDto capNhatSoLuong(HttpSession session, Integer sanPhamChiTietId, Integer soLuong);
    CartDto xoaSanPham(HttpSession session, Integer sanPhamChiTietId);
    void xoaTatCa(HttpSession session);
    int getTongSoLuong(HttpSession session);
    void dongBoVaoDatabase(HttpSession session, Integer khachHangId);
}
