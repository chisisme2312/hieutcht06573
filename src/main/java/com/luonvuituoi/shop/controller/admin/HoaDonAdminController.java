package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import com.luonvuituoi.shop.service.BanHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/hoa-don")
@RequiredArgsConstructor
public class HoaDonAdminController {
    private final HoaDonRepository hoaDonRepository;
    private final BanHangService banHangService;

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("hoaDonList", hoaDonRepository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getNgayTao() == null) return 1;
                    if (b.getNgayTao() == null) return -1;
                    return b.getNgayTao().compareTo(a.getNgayTao());
                }).toList());
        return "admin/hoa-don/index";
    }

    @GetMapping("/{id}")
    public String chiTiet(@PathVariable Integer id, Model model) {
        HoaDon hd = hoaDonRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn."));
        model.addAttribute("hoaDon", hd);
        model.addAttribute("chiTietHoaDon", banHangService.layChiTiet(id));
        return "admin/hoa-don/chi-tiet";
    }
}
