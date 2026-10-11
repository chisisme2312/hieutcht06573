package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    Optional<KhachHang> findByEmail(String email);
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);
}
