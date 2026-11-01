package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    List<HoaDon> findByTrangThai(String trangThai);
    List<HoaDon> findByKhachHangId(Integer khachHangId);
    List<HoaDon> findByHinhThucMuaHangTrueOrderByNgayTaoDesc();

    @Override
    @EntityGraph(attributePaths = {"khachHang", "nhanVien"})
    Page<HoaDon> findAll(Pageable pageable);
}
