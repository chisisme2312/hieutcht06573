package com.luonvuituoi.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dia_chi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private KhachHang khachHang;

    @Column(name = "duong")
    private String duong;

    @Column(name = "xa_phuong")
    private String xaPhuong;

    @Column(name = "quan_huyen")
    private String quanHuyen;

    @Column(name = "tinh_thanh_pho")
    private String tinhThanhPho;

    @Column(name = "mac_dinh")
    private Boolean macDinh;

    public String getDiaChiDayDu() {
        StringBuilder sb = new StringBuilder();
        if (duong != null && !duong.isBlank()) sb.append(duong.trim());
        if (xaPhuong != null && !xaPhuong.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(xaPhuong.trim());
        }
        if (quanHuyen != null && !quanHuyen.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(quanHuyen.trim());
        }
        if (tinhThanhPho != null && !tinhThanhPho.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(tinhThanhPho.trim());
        }
        return sb.toString();
    }
}
