package com.luonvuituoi.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {

    @Builder.Default
    private List<CartItemDto> items = new ArrayList<>();

    public int getTongSoLuong() {
        if (items == null) return 0;
        return items.stream().mapToInt(item -> item.getSoLuong() != null ? item.getSoLuong() : 0).sum();
    }

    public BigDecimal getTamTinh() {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(CartItemDto::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Miễn phí vận chuyển cho đơn hàng từ 399.000đ trở lên
    public BigDecimal getPhiVanChuyen() {
        BigDecimal tamTinh = getTamTinh();
        if (tamTinh.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        if (tamTinh.compareTo(BigDecimal.valueOf(399000)) >= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(30000);
    }

    public BigDecimal getTongTien() {
        return getTamTinh().add(getPhiVanChuyen());
    }

    // Tính phần trăm để đạt mốc Freeship (399.000đ)
    public int getTienDoFreeship() {
        BigDecimal tamTinh = getTamTinh();
        BigDecimal mucFreeship = BigDecimal.valueOf(399000);
        if (tamTinh.compareTo(mucFreeship) >= 0) return 100;
        return tamTinh.multiply(BigDecimal.valueOf(100)).divide(mucFreeship, java.math.RoundingMode.HALF_UP).intValue();
    }

    public BigDecimal getTienConThieuFreeship() {
        BigDecimal tamTinh = getTamTinh();
        BigDecimal mucFreeship = BigDecimal.valueOf(399000);
        if (tamTinh.compareTo(mucFreeship) >= 0) return BigDecimal.ZERO;
        return mucFreeship.subtract(tamTinh);
    }
}
