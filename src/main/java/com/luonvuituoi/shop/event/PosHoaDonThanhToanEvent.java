package com.luonvuituoi.shop.event;

public class PosHoaDonThanhToanEvent {

    private final Integer hoaDonId;

    public PosHoaDonThanhToanEvent(Integer hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public Integer getHoaDonId() {
        return hoaDonId;
    }
}