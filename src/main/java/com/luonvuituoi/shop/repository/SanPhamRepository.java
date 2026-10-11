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

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.danhSachAnh LEFT JOIN FETCH sp.danhSachChiTiet ct LEFT JOIN FETCH ct.mauSac LEFT JOIN FETCH ct.kichThuoc WHERE sp.trangThai = true")
    List<SanPham> findAllActiveWithDetails();
}
