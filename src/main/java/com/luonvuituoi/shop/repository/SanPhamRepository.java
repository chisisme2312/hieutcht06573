package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findByTenSanPhamContainingIgnoreCase(String keyword);
    List<SanPham> findByTrangThaiTrue();

    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.danhSachAnh " +
           "LEFT JOIN FETCH sp.danhSachChiTiet ct " +
           "LEFT JOIN FETCH ct.mauSac " +
           "LEFT JOIN FETCH ct.kichThuoc " +
           "WHERE sp.trangThai = true")
    List<SanPham> findAllActiveWithDetails();

    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.danhSachAnh " +
           "LEFT JOIN FETCH sp.danhSachChiTiet ct " +
           "LEFT JOIN FETCH ct.mauSac " +
           "LEFT JOIN FETCH ct.kichThuoc " +
           "WHERE sp.id = :id AND sp.trangThai = true")
    Optional<SanPham> findActiveByIdWithDetails(@Param("id") Integer id);

    List<SanPham> findTop12ByTrangThaiTrueOrderByIdDesc();

    List<SanPham> findTop8ByLoaiSanPhamIdAndIdNotAndTrangThaiTrue(Integer loaiSanPhamId, Integer id);

    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "JOIN sp.danhSachChiTiet ct " +
           "WHERE sp.trangThai = true AND ct.khuyenMai IS NOT NULL")
    List<SanPham> findTopFeatured(Pageable pageable);

    @Query(value = "SELECT sp FROM SanPham sp WHERE " +
            "(:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) AND " +
            "(:trangThai IS NULL OR sp.trangThai = :trangThai)",
            countQuery = "SELECT COUNT(sp) FROM SanPham sp WHERE " +
            "(:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) AND " +
            "(:trangThai IS NULL OR sp.trangThai = :trangThai)")
    Page<SanPham> filterSanPham(@Param("keyword") String keyword,
                                @Param("loaiSanPhamId") Integer loaiSanPhamId,
                                @Param("trangThai") Boolean trangThai,
                                Pageable pageable);

    // Bộ lọc đa năng phía Khách hàng (Client Filter)
    @Query(value = "SELECT sp FROM SanPham sp WHERE sp.trangThai = true " +
            "AND (:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) " +
            "AND EXISTS (" +
            "   SELECT 1 FROM SanPhamChiTiet ct WHERE ct.sanPham = sp AND ct.trangThai = true " +
            "   AND (:mauSacId IS NULL OR ct.mauSac.id = :mauSacId) " +
            "   AND (:kichThuocId IS NULL OR ct.kichThuoc.id = :kichThuocId) " +
            "   AND (:minGia IS NULL OR ct.giaBan >= :minGia) " +
            "   AND (:maxGia IS NULL OR ct.giaBan <= :maxGia)" +
            ")",
           countQuery = "SELECT COUNT(sp) FROM SanPham sp WHERE sp.trangThai = true " +
            "AND (:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) " +
            "AND EXISTS (" +
            "   SELECT 1 FROM SanPhamChiTiet ct WHERE ct.sanPham = sp AND ct.trangThai = true " +
            "   AND (:mauSacId IS NULL OR ct.mauSac.id = :mauSacId) " +
            "   AND (:kichThuocId IS NULL OR ct.kichThuoc.id = :kichThuocId) " +
            "   AND (:minGia IS NULL OR ct.giaBan >= :minGia) " +
            "   AND (:maxGia IS NULL OR ct.giaBan <= :maxGia)" +
            ")")
    Page<SanPham> filterClientSanPham(
            @Param("keyword") String keyword,
            @Param("loaiSanPhamId") Integer loaiSanPhamId,
            @Param("mauSacId") Integer mauSacId,
            @Param("kichThuocId") Integer kichThuocId,
            @Param("minGia") BigDecimal minGia,
            @Param("maxGia") BigDecimal maxGia,
            Pageable pageable);

    @Query(value = "SELECT sp FROM SanPham sp WHERE sp.trangThai = true " +
            "AND (:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) " +
            "AND EXISTS (" +
            "   SELECT 1 FROM SanPhamChiTiet ct WHERE ct.sanPham = sp AND ct.trangThai = true " +
            "   AND (:mauSacId IS NULL OR ct.mauSac.id = :mauSacId) " +
            "   AND (:kichThuocId IS NULL OR ct.kichThuoc.id = :kichThuocId) " +
            "   AND (:minGia IS NULL OR ct.giaBan >= :minGia) " +
            "   AND (:maxGia IS NULL OR ct.giaBan <= :maxGia)" +
            ") " +
            "ORDER BY (SELECT MIN(ct.giaBan) FROM SanPhamChiTiet ct WHERE ct.sanPham = sp) ASC",
           countQuery = "SELECT COUNT(sp) FROM SanPham sp WHERE sp.trangThai = true " +
            "AND (:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) " +
            "AND EXISTS (" +
            "   SELECT 1 FROM SanPhamChiTiet ct WHERE ct.sanPham = sp AND ct.trangThai = true " +
            "   AND (:mauSacId IS NULL OR ct.mauSac.id = :mauSacId) " +
            "   AND (:kichThuocId IS NULL OR ct.kichThuoc.id = :kichThuocId) " +
            "   AND (:minGia IS NULL OR ct.giaBan >= :minGia) " +
            "   AND (:maxGia IS NULL OR ct.giaBan <= :maxGia)" +
            ")")
    Page<SanPham> filterClientSanPhamPriceAsc(
            @Param("keyword") String keyword,
            @Param("loaiSanPhamId") Integer loaiSanPhamId,
            @Param("mauSacId") Integer mauSacId,
            @Param("kichThuocId") Integer kichThuocId,
            @Param("minGia") BigDecimal minGia,
            @Param("maxGia") BigDecimal maxGia,
            Pageable pageable);

    @Query(value = "SELECT sp FROM SanPham sp WHERE sp.trangThai = true " +
            "AND (:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) " +
            "AND EXISTS (" +
            "   SELECT 1 FROM SanPhamChiTiet ct WHERE ct.sanPham = sp AND ct.trangThai = true " +
            "   AND (:mauSacId IS NULL OR ct.mauSac.id = :mauSacId) " +
            "   AND (:kichThuocId IS NULL OR ct.kichThuoc.id = :kichThuocId) " +
            "   AND (:minGia IS NULL OR ct.giaBan >= :minGia) " +
            "   AND (:maxGia IS NULL OR ct.giaBan <= :maxGia)" +
            ") " +
            "ORDER BY (SELECT MAX(ct.giaBan) FROM SanPhamChiTiet ct WHERE ct.sanPham = sp) DESC",
           countQuery = "SELECT COUNT(sp) FROM SanPham sp WHERE sp.trangThai = true " +
            "AND (:keyword IS NULL OR LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:loaiSanPhamId IS NULL OR sp.loaiSanPham.id = :loaiSanPhamId) " +
            "AND EXISTS (" +
            "   SELECT 1 FROM SanPhamChiTiet ct WHERE ct.sanPham = sp AND ct.trangThai = true " +
            "   AND (:mauSacId IS NULL OR ct.mauSac.id = :mauSacId) " +
            "   AND (:kichThuocId IS NULL OR ct.kichThuoc.id = :kichThuocId) " +
            "   AND (:minGia IS NULL OR ct.giaBan >= :minGia) " +
            "   AND (:maxGia IS NULL OR ct.giaBan <= :maxGia)" +
            ")")
    Page<SanPham> filterClientSanPhamPriceDesc(
            @Param("keyword") String keyword,
            @Param("loaiSanPhamId") Integer loaiSanPhamId,
            @Param("mauSacId") Integer mauSacId,
            @Param("kichThuocId") Integer kichThuocId,
            @Param("minGia") BigDecimal minGia,
            @Param("maxGia") BigDecimal maxGia,
            Pageable pageable);
}
