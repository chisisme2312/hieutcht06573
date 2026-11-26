package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {
    List<SanPhamChiTiet> findBySanPhamId(Integer sanPhamId);
    List<SanPhamChiTiet> findBySanPhamIdAndTrangThaiTrue(Integer sanPhamId);
    Optional<SanPhamChiTiet> findBySanPhamIdAndMauSacIdAndKichThuocId(Integer sanPhamId, Integer mauSacId, Integer kichThuocId);

    // Trừ tồn kho nguyên tử (Atomic deduction) - chống race condition khi nhiều người cùng mua cùng lúc
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE SanPhamChiTiet ct SET ct.soLuongTon = ct.soLuongTon - :soLuong WHERE ct.id = :id AND ct.soLuongTon >= :soLuong")
    int truTonKhoAtomic(@org.springframework.data.repository.query.Param("id") Integer id, @org.springframework.data.repository.query.Param("soLuong") Integer soLuong);

    // Hoàn trả tồn kho khi đơn hàng bị hủy
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE SanPhamChiTiet ct SET ct.soLuongTon = ct.soLuongTon + :soLuong WHERE ct.id = :id")
    int hoanTonKhoAtomic(@org.springframework.data.repository.query.Param("id") Integer id, @org.springframework.data.repository.query.Param("soLuong") Integer soLuong);

    // Tự động chuyển ngừng hoạt động khi tồn kho = 0
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE SanPhamChiTiet ct SET ct.trangThai = false WHERE ct.id = :id AND ct.soLuongTon <= 0")
    int capNhatHetHang(@org.springframework.data.repository.query.Param("id") Integer id);

    // Tự động chuyển đang hoạt động khi nhập thêm hàng (tồn kho > 0)
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @org.springframework.data.jpa.repository.Query("UPDATE SanPhamChiTiet ct SET ct.trangThai = true WHERE ct.id = :id AND ct.soLuongTon > 0")
    int capNhatConHang(@org.springframework.data.repository.query.Param("id") Integer id);
}
