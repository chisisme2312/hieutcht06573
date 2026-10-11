package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.entity.HoaDon;
import java.util.List;
import java.util.Optional;

public interface HoaDonService {
    // TODO: Thành viên phụ trách Hóa đơn / Bán hàng sẽ hoàn thiện
    List<HoaDon> getAll();
    Optional<HoaDon> getById(Integer id);
    HoaDon taoHoaDon(HoaDon hoaDon);
    void capNhatTrangThai(Integer id, String trangThai);
}
