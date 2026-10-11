package com.luonvuituoi.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_hoa_don", nullable = false, unique = true)
    private String maHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_giam_gia")
    private GiamGia giamGia;

    @Column(name = "tong_tien", nullable = false)
    private BigDecimal tongTien;

    @Column(name = "tien_truoc_giam")
    private BigDecimal tienTruocGiam;

    @Column(name = "phi_giao_hang")
    private BigDecimal phiGiaoHang;

    @Column(name = "phuong_thuc_thanh_toan")
    private Boolean phuongThucThanhToan;

    @Column(name = "hinh_thuc_mua_hang")
    private Boolean hinhThucMuaHang;

    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL)
    private List<HoaDonChiTiet> danhSachChiTiet;
}
