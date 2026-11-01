package com.luonvuituoi.shop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.luonvuituoi.shop.entity.DiaChi; // Đảm bảo đúng tên Entity của bạn

@Repository
public interface Dcrepository extends JpaRepository<DiaChi, Integer> {
}

