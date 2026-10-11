package com.luonvuituoi.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "co_ao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoAo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_co_ao", nullable = false, unique = true)
    private String tenCoAo;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}
