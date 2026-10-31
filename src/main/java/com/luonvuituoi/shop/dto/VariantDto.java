package com.luonvuituoi.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantDto {
    private Integer id;
    private String maSanPhamChiTiet;
    private Integer mauSacId;
    private String tenMauSac;
    private Integer kichThuocId;
    private String tenKichThuoc;
    private BigDecimal giaBan;
    private BigDecimal giaBanBanDau;
    private Integer soLuongTon;
    private Boolean trangThai;
}
