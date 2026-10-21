package com.luonvuituoi.shop.service.impl;

import com.luonvuituoi.shop.entity.*;
import com.luonvuituoi.shop.repository.*;
import com.luonvuituoi.shop.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;

    @Override
    public List<SanPham> getAll() {
        return sanPhamRepository.findAll();
    }

    @Override
    public List<SanPham> getActiveWithDetails() {
        return sanPhamRepository.findAllActiveWithDetails();
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

    @Override
    @Transactional
    public SanPham saveWithVariants(SanPham sanPham, 
                                    List<Integer> listMauSacId, 
                                    List<Integer> listKichThuocId, 
                                    BigDecimal giaBan, 
                                    BigDecimal giaBanBanDau, 
                                    Integer soLuongTon) {
        if (sanPham.getId() == null && sanPham.getNgayTao() == null) {
            sanPham.setNgayTao(LocalDateTime.now());
        }
        if (sanPham.getTrangThai() == null) {
            sanPham.setTrangThai(true);
        }

        // Lưu sản phẩm cha trước
        SanPham savedSp = sanPhamRepository.save(sanPham);

        // Sinh các biến thể chi tiết kết hợp giữa Màu Sắc và Kích Thước
        if (listMauSacId != null && !listMauSacId.isEmpty() && listKichThuocId != null && !listKichThuocId.isEmpty()) {
            for (Integer idMau : listMauSacId) {
                MauSac ms = mauSacRepository.findById(idMau).orElse(null);
                if (ms == null) continue;

                for (Integer idSize : listKichThuocId) {
                    KichThuoc kt = kichThuocRepository.findById(idSize).orElse(null);
                    if (kt == null) continue;

                    String maCt = savedSp.getMaSanPham() + "-" + ms.getId() + "-" + kt.getId();
                    
                    SanPhamChiTiet ct = SanPhamChiTiet.builder()
                            .maSanPhamChiTiet(maCt)
                            .sanPham(savedSp)
                            .mauSac(ms)
                            .kichThuoc(kt)
                            .giaBan(giaBan != null ? giaBan : BigDecimal.ZERO)
                            .giaBanBanDau(giaBanBanDau != null ? giaBanBanDau : giaBan)
                            .soLuongTon(soLuongTon != null ? soLuongTon : 10)
                            .trangThai(true)
                            .build();

                    sanPhamChiTietRepository.save(ct);
                }
            }
        }

        return savedSp;
    }
}
