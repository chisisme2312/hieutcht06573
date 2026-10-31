package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {
    List<SanPhamChiTiet> findBySanPhamId(Integer sanPhamId);
    List<SanPhamChiTiet> findBySanPhamIdAndTrangThaiTrue(Integer sanPhamId);
    Optional<SanPhamChiTiet> findBySanPhamIdAndMauSacIdAndKichThuocId(Integer sanPhamId, Integer mauSacId, Integer kichThuocId);
}
