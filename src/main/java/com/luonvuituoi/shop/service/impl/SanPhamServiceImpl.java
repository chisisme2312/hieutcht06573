package com.luonvuituoi.shop.service.impl;

import com.luonvuituoi.shop.entity.*;
import com.luonvuituoi.shop.repository.*;
import com.luonvuituoi.shop.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

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
    public Optional<SanPham> getByIdWithDetails(Integer id) {
        return sanPhamRepository.findActiveByIdWithDetails(id);
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
    public Page<SanPham> filterSanPham(String keyword, Integer loaiSanPhamId, Boolean trangThai, int page, int size) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return sanPhamRepository.filterSanPham(cleanKeyword, loaiSanPhamId, trangThai, pageable);
    }

    @Override
    public Page<SanPham> filterClientSanPham(String keyword, Integer loaiSanPhamId, Integer mauSacId, Integer kichThuocId,
                                            BigDecimal minGia, BigDecimal maxGia, String sort, int page, int size) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        if ("gia-tang".equalsIgnoreCase(sort)) {
            Pageable pageable = PageRequest.of(page, size);
            return sanPhamRepository.filterClientSanPhamPriceAsc(cleanKeyword, loaiSanPhamId, mauSacId, kichThuocId, minGia, maxGia, pageable);
        } else if ("gia-giam".equalsIgnoreCase(sort)) {
            Pageable pageable = PageRequest.of(page, size);
            return sanPhamRepository.filterClientSanPhamPriceDesc(cleanKeyword, loaiSanPhamId, mauSacId, kichThuocId, minGia, maxGia, pageable);
        } else if ("ten-az".equalsIgnoreCase(sort)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "tenSanPham"));
            return sanPhamRepository.filterClientSanPham(cleanKeyword, loaiSanPhamId, mauSacId, kichThuocId, minGia, maxGia, pageable);
        } else {
            // Mặc định hoặc "moi-nhat"
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            return sanPhamRepository.filterClientSanPham(cleanKeyword, loaiSanPhamId, mauSacId, kichThuocId, minGia, maxGia, pageable);
        }
    }

    @Override
    public List<SanPham> getSanPhamMoiVe(int limit) {
        List<SanPham> list = sanPhamRepository.findTop12ByTrangThaiTrueOrderByIdDesc();
        if (list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }

    @Override
    public List<SanPham> getSanPhamNoiBat(int limit) {
        List<SanPham> featured = sanPhamRepository.findTopFeatured(PageRequest.of(0, limit));
        if (featured.size() < limit) {
            Set<Integer> existingIds = featured.stream().map(SanPham::getId).collect(Collectors.toSet());
            List<SanPham> allActive = sanPhamRepository.findTop12ByTrangThaiTrueOrderByIdDesc();
            for (SanPham sp : allActive) {
                if (!existingIds.contains(sp.getId())) {
                    featured.add(sp);
                    existingIds.add(sp.getId());
                    if (featured.size() >= limit) break;
                }
            }
        }
        return featured;
    }

    @Override
    public List<SanPham> getSanPhamBanChay(int limit) {
        try {
            List<Object[]> topIdsResult = hoaDonChiTietRepository.findTopSellingProductIds(PageRequest.of(0, limit));
            if (topIdsResult != null && !topIdsResult.isEmpty()) {
                List<Integer> ids = topIdsResult.stream()
                        .map(row -> (Integer) row[0])
                        .collect(Collectors.toList());
                List<SanPham> products = sanPhamRepository.findAllById(ids);
                Map<Integer, SanPham> productMap = products.stream()
                        .collect(Collectors.toMap(SanPham::getId, sp -> sp));
                List<SanPham> sorted = new ArrayList<>();
                for (Integer id : ids) {
                    SanPham sp = productMap.get(id);
                    if (sp != null && Boolean.TRUE.equals(sp.getTrangThai())) {
                        sorted.add(sp);
                    }
                }
                if (!sorted.isEmpty()) {
                    return sorted;
                }
            }
        } catch (Exception ignored) {
        }
        return getSanPhamMoiVe(limit);
    }

    @Override
    public List<SanPham> getSanPhamLienQuan(Integer loaiSanPhamId, Integer currentProductId, int limit) {
        if (loaiSanPhamId == null) {
            return Collections.emptyList();
        }
        List<SanPham> list = sanPhamRepository.findTop8ByLoaiSanPhamIdAndIdNotAndTrangThaiTrue(loaiSanPhamId, currentProductId);
        if (list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }

    @Override
    @Transactional
    public void dongBoTrangThaiBienThe(Integer sanPhamId, boolean trangThaiSanPham) {
        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPhamId(sanPhamId);
        if (variants != null && !variants.isEmpty()) {
            for (SanPhamChiTiet ct : variants) {
                if (!trangThaiSanPham) {
                    ct.setTrangThai(false);
                } else {
                    if (ct.getSoLuongTon() != null && ct.getSoLuongTon() > 0) {
                        ct.setTrangThai(true);
                    } else {
                        ct.setTrangThai(false);
                    }
                }
                sanPhamChiTietRepository.save(ct);
            }
        }
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

        SanPham savedSanPham = sanPhamRepository.save(sanPham);

        if (listMauSacId != null && listKichThuocId != null && !listMauSacId.isEmpty() && !listKichThuocId.isEmpty()) {
            int counter = 1;
            for (Integer mauSacId : listMauSacId) {
                MauSac mauSac = mauSacRepository.findById(mauSacId).orElse(null);
                if (mauSac == null) continue;

                for (Integer kichThuocId : listKichThuocId) {
                    KichThuoc kichThuoc = kichThuocRepository.findById(kichThuocId).orElse(null);
                    if (kichThuoc == null) continue;

                    String sku = String.format("%s-%s-%s-%03d",
                            savedSanPham.getMaSanPham(),
                            kichThuoc.getTenKichThuoc(),
                            mauSac.getTenMauSac().replaceAll("\\s+", ""),
                            counter++);

                    SanPhamChiTiet variant = SanPhamChiTiet.builder()
                            .sanPham(savedSanPham)
                            .mauSac(mauSac)
                            .kichThuoc(kichThuoc)
                            .maSanPhamChiTiet(sku)
                            .giaBan(giaBan != null ? giaBan : BigDecimal.ZERO)
                            .giaBanBanDau(giaBanBanDau != null ? giaBanBanDau : (giaBan != null ? giaBan : BigDecimal.ZERO))
                            .soLuongTon(soLuongTon != null ? soLuongTon : 0)
                            .trangThai(savedSanPham.getTrangThai() && (soLuongTon != null && soLuongTon > 0))
                            .build();

                    sanPhamChiTietRepository.save(variant);
                }
            }
        }

        return savedSanPham;
    }
}
