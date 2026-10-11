package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    List<HoaDon> findByTrangThai(String trangThai);
    List<HoaDon> findByKhachHangId(Integer khachHangId);
}
