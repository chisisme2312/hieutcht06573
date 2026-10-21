package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.entity.Anh;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import java.util.List;
import java.util.Optional;

public interface SanPhamChiTietService {
    List<SanPhamChiTiet> getBySanPhamId(Integer sanPhamId);
    Optional<SanPhamChiTiet> getById(Integer id);
    SanPhamChiTiet save(SanPhamChiTiet chiTiet);
    void delete(Integer id);

    // Quản lý ảnh
    List<Anh> getAnhBySanPhamId(Integer sanPhamId);
    Anh saveAnh(Anh anh);
    void deleteAnh(Integer id);
}
