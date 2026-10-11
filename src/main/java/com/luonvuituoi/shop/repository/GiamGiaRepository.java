package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.GiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiamGiaRepository extends JpaRepository<GiamGia, Integer> {
    Optional<GiamGia> findByMaGiamGiaAndTrangThaiTrue(String maGiamGia);
}
