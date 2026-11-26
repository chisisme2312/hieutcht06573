package com.luonvuituoi.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.luonvuituoi.shop.entity.DiaChi;

import java.util.List;
import java.util.Optional;

@Repository
public interface Dcrepository extends JpaRepository<DiaChi, Integer> {
    List<DiaChi> findByKhachHangId(Integer khachHangId);

    Optional<DiaChi> findFirstByKhachHangIdAndMacDinhTrue(Integer khachHangId);

    Optional<DiaChi> findFirstByKhachHangIdOrderByIdDesc(Integer khachHangId);

    Optional<DiaChi> findFirstByKhachHangId(Integer khachHangId);
}


