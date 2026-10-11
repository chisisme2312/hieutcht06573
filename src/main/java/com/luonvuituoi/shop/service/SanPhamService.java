package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.entity.SanPham;
import java.util.List;
import java.util.Optional;

public interface SanPhamService {
    // TODO: Thành viên phụ trách Sản phẩm sẽ hoàn thiện các hàm này
    List<SanPham> getAll();
    List<SanPham> getActiveWithDetails();
    Optional<SanPham> getById(Integer id);
    SanPham save(SanPham sanPham);
    void delete(Integer id);
    List<SanPham> searchByName(String keyword);
}
