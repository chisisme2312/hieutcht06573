package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    // TODO: Viết thêm hàm tìm kiếm theo tên, theo mã sản phẩm
    List<SanPham> findByTenSanPhamContainingIgnoreCase(String keyword);
    List<SanPham> findByTrangThaiTrue();
}
