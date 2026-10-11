package com.luonvuituoi.shop.service.impl;

import com.luonvuituoi.shop.entity.SanPham;
import com.luonvuituoi.shop.repository.SanPhamRepository;
import com.luonvuituoi.shop.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;

    @Override
    public List<SanPham> getAll() {
        return sanPhamRepository.findAll();
    }

    @Override
    public Optional<SanPham> getById(Integer id) {
        return sanPhamRepository.findById(id);
    }

    @Override
    public SanPham save(SanPham sanPham) {
        return sanPhamRepository.save(sanPham);
    }

    @Override
    public void delete(Integer id) {
        sanPhamRepository.deleteById(id);
    }

    @Override
    public List<SanPham> searchByName(String keyword) {
        return sanPhamRepository.findByTenSanPhamContainingIgnoreCase(keyword);
    }
}
