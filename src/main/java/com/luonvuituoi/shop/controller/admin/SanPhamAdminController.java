package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/san-pham")
@RequiredArgsConstructor
public class SanPhamAdminController {

    private final SanPhamService sanPhamService;

    // TODO: Thành viên phụ trách Quản lý sản phẩm code ở đây
    @GetMapping
    public String danhSachSanPham(Model model) {
        model.addAttribute("danhSach", sanPhamService.getAll());
        return "admin/san-pham/index";
    }
}
