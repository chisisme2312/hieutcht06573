package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.entity.SanPham;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SanPhamService {
    List<SanPham> getAll();
    List<SanPham> getActiveWithDetails();
    Optional<SanPham> getById(Integer id);
    SanPham save(SanPham sanPham);
    void delete(Integer id);
    List<SanPham> searchByName(String keyword);

    // Lưu sản phẩm kèm biến thể (màu sắc, kích thước, giá, số lượng)
    SanPham saveWithVariants(SanPham sanPham, 
                             List<Integer> listMauSacId, 
                             List<Integer> listKichThuocId, 
                             BigDecimal giaBan, 
                             BigDecimal giaBanBanDau, 
                             Integer soLuongTon);
}
