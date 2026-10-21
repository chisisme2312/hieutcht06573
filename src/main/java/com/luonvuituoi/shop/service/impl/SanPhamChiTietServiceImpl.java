package com.luonvuituoi.shop.service.impl;

import com.luonvuituoi.shop.entity.Anh;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.repository.AnhRepository;
import com.luonvuituoi.shop.repository.SanPhamChiTietRepository;
import com.luonvuituoi.shop.service.SanPhamChiTietService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanPhamChiTietServiceImpl implements SanPhamChiTietService {

    private final SanPhamChiTietRepository chiTietRepository;
    private final AnhRepository anhRepository;

    @Override
    public List<SanPhamChiTiet> getBySanPhamId(Integer sanPhamId) {
        return chiTietRepository.findBySanPhamId(sanPhamId);
    }

    @Override
    public Optional<SanPhamChiTiet> getById(Integer id) {
        return chiTietRepository.findById(id);
    }

    @Override
    public SanPhamChiTiet save(SanPhamChiTiet chiTiet) {
        if (chiTiet.getTrangThai() == null) {
            chiTiet.setTrangThai(true);
        }
        return chiTietRepository.save(chiTiet);
    }

    @Override
    public void delete(Integer id) {
        chiTietRepository.deleteById(id);
    }

    @Override
    public List<Anh> getAnhBySanPhamId(Integer sanPhamId) {
        return anhRepository.findBySanPhamId(sanPhamId);
    }

    @Override
    public Anh saveAnh(Anh anh) {
        if (anh.getId() == null && anh.getNgayTao() == null) {
            anh.setNgayTao(LocalDateTime.now());
        }
        if (anh.getTrangThai() == null) {
            anh.setTrangThai(true);
        }
        return anhRepository.save(anh);
    }

    @Override
    public void deleteAnh(Integer id) {
        anhRepository.deleteById(id);
    }
}
