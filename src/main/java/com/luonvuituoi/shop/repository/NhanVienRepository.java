package com.luonvuituoi.shop.repository;

import com.luonvuituoi.shop.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByTenDangNhap(String tenDangNhap);
    Optional<NhanVien> findByEmail(String email);
    Optional<NhanVien> findByMaNhanVien(String maNhanVien);
    List<NhanVien> findByTrangThaiTrue();
    List<NhanVien> findAllByOrderByNgayTaoDesc();
    List<NhanVien> findByHoVaTenContainingIgnoreCaseOrTenDangNhapContainingIgnoreCaseOrSoDienThoaiContainingIgnoreCaseOrderByNgayTaoDesc(String ten, String username, String sdt);
}
