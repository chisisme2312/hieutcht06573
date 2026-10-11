package com.luonvuituoi.shop.service.impl;

import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import com.luonvuituoi.shop.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;

    @Override
    public List<HoaDon> getAll() {
        return hoaDonRepository.findAll();
    }

    @Override
    public Optional<HoaDon> getById(Integer id) {
        return hoaDonRepository.findById(id);
    }

    @Override
    public HoaDon taoHoaDon(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public void capNhatTrangThai(Integer id, String trangThai) {
        hoaDonRepository.findById(id).ifPresent(hd -> {
            hd.setTrangThai(trangThai);
            hoaDonRepository.save(hd);
        });
    }
}
