package com.luonvuituoi.shop.controller.client;

import com.luonvuituoi.shop.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SanPhamService sanPhamService;

    // TODO: Thành viên làm Giao diện khách hàng (Client) code ở đây
    @GetMapping("/")
    public String trangChu(Model model) {
        model.addAttribute("danhSachSanPham", sanPhamService.getAll());
        return "client/index";
    }
}
