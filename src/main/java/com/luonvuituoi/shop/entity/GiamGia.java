package com.luonvuituoi.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_giam_gia", nullable = false, unique = true)
    private String maGiamGia;

    @Column(name = "ten_giam_gia", nullable = false)
    private String tenGiamGia;

    @Column(name = "loai_giam_gia")
    private Integer loaiGiamGia;

    @Column(name = "gia_tri_giam", nullable = false)
    private BigDecimal giaTriGiam;

    @Column(name = "toi_da_giam_gia")
    private BigDecimal toiDaGiamGia;

    @Column(name = "gia_tri_toi_thieu")
    private BigDecimal giaTriToiThieu;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}
