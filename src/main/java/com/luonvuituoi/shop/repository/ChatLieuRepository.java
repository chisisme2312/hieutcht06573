package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {
}
