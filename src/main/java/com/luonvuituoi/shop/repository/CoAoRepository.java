package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.CoAo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoAoRepository extends JpaRepository<CoAo, Integer> {
}
