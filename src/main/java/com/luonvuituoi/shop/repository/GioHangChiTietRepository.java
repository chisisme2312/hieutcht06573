package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
    List<GioHangChiTiet> findByKhachHangId(Integer khachHangId);
    Optional<GioHangChiTiet> findByKhachHangIdAndSanPhamChiTietId(Integer khachHangId, Integer sanPhamChiTietId);
    void deleteByKhachHangId(Integer khachHangId);
    void deleteByKhachHangIdAndSanPhamChiTietId(Integer khachHangId, Integer sanPhamChiTietId);
}
