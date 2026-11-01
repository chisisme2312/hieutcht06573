package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.entity.SanPham;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SanPhamService {
    List<SanPham> getAll();
    List<SanPham> getActiveWithDetails();
    Optional<SanPham> getById(Integer id);
    Optional<SanPham> getByIdWithDetails(Integer id);
    SanPham save(SanPham sanPham);
    void delete(Integer id);
    List<SanPham> searchByName(String keyword);

    // Phân trang và lọc đa tiêu chí phía Admin
    Page<SanPham> filterSanPham(String keyword, Integer loaiSanPhamId, Boolean trangThai, int page, int size);

    // Phân trang và lọc đa tiêu chí phía Khách hàng (Client)
    Page<SanPham> filterClientSanPham(String keyword, Integer loaiSanPhamId, Integer mauSacId, Integer kichThuocId, BigDecimal minGia, BigDecimal maxGia, String sort, int page, int size);

    // Danh sách sản phẩm cho Trang chủ
    List<SanPham> getSanPhamMoiVe(int limit);
    List<SanPham> getSanPhamNoiBat(int limit);
    List<SanPham> getSanPhamBanChay(int limit);

    // Sản phẩm liên quan
    List<SanPham> getSanPhamLienQuan(Integer loaiSanPhamId, Integer currentProductId, int limit);

    // Đồng bộ trạng thái từ mẫu áo cha sang các biến thể con
    void dongBoTrangThaiBienThe(Integer sanPhamId, boolean trangThaiSanPham);

    // Lưu sản phẩm kèm biến thể (màu sắc, kích thước, giá, số lượng)
    SanPham saveWithVariants(SanPham sanPham, 
                             List<Integer> listMauSacId, 
                             List<Integer> listKichThuocId, 
                             BigDecimal giaBan, 
                             BigDecimal giaBanBanDau, 
                             Integer soLuongTon);
}
