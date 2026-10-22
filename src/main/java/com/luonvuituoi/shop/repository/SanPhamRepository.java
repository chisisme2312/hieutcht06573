package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    
    List<SanPham> findByTenSanPhamContainingIgnoreCase(String keyword);
    List<SanPham> findByTrangThaiTrue();

    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.danhSachAnh LEFT JOIN FETCH sp.danhSachChiTiet ct LEFT JOIN FETCH ct.mauSac LEFT JOIN FETCH ct.kichThuoc WHERE sp.trangThai = true")
    List<SanPham> findAllActiveWithDetails();

    @Query(value = "SELECT sp FROM SanPham sp WHERE " +
            "(:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) AND " +
            "(:trangThai IS NULL OR sp.trangThai = :trangThai)",
            countQuery = "SELECT COUNT(sp) FROM SanPham sp WHERE " +
            "(:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) AND " +
            "(:trangThai IS NULL OR sp.trangThai = :trangThai)")
    Page<SanPham> filterSanPham(@Param("keyword") String keyword,
                                @Param("loaiSanPhamId") Integer loaiSanPhamId,
                                @Param("trangThai") Boolean trangThai,
                                Pageable pageable);
}
