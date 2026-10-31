package com.luonvuituoi.shop.controller.client;

import com.luonvuituoi.shop.service.SanPhamService;
import com.luonvuituoi.shop.service.ThuocTinhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SanPhamService sanPhamService;
    private final ThuocTinhService thuocTinhService;

    @GetMapping("/")
    public String trangChu(Model model) {
        // 1. Sản phẩm Mới Về (New Arrivals)
        model.addAttribute("sanPhamMoiVe", sanPhamService.getSanPhamMoiVe(8));

        // 2. Sản phẩm Nổi Bật (Featured / Khuyến Mãi)
        model.addAttribute("sanPhamNoiBat", sanPhamService.getSanPhamNoiBat(8));

        // 3. Sản phẩm Bán Chạy (Best Sellers)
        model.addAttribute("sanPhamBanChay", sanPhamService.getSanPhamBanChay(8));

        // 4. Danh mục loại áo để hiển thị quick category cards
        model.addAttribute("danhSachLoai", thuocTinhService.getAllLoaiSanPham());

        return "client/index";
    }
}
