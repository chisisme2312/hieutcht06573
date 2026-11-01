package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.HoaDonChiTiet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    List<HoaDonChiTiet> findByHoaDonId(Integer hoaDonId);

    @Query("SELECT hdct.sanPhamChiTiet.sanPham.id, SUM(hdct.soLuong) " +
           "FROM HoaDonChiTiet hdct " +
           "WHERE hdct.sanPhamChiTiet.sanPham.trangThai = true " +
           "GROUP BY hdct.sanPhamChiTiet.sanPham.id " +
           "ORDER BY SUM(hdct.soLuong) DESC")
    List<Object[]> findTopSellingProductIds(Pageable pageable);
}
