package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.Anh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnhRepository extends JpaRepository<Anh, Integer> {
    List<Anh> findBySanPhamId(Integer sanPhamId);
}
