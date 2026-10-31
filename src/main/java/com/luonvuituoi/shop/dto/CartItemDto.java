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
public class CartItemDto {
    private Integer sanPhamChiTietId;
    private Integer sanPhamId;
    private String maSanPham;
    private String tenSanPham;
    private String anhUrl;
    private Integer mauSacId;
    private String tenMauSac;
    private Integer kichThuocId;
    private String tenKichThuoc;
    private BigDecimal giaBan;
    private BigDecimal giaBanBanDau;
    private Integer soLuong;
    private Integer soLuongTon;

    public BigDecimal getThanhTien() {
        if (giaBan == null || soLuong == null) return BigDecimal.ZERO;
        return giaBan.multiply(BigDecimal.valueOf(soLuong));
    }
}
