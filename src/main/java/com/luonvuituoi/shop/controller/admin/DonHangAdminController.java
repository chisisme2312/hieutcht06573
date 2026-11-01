package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.repository.GiamGiaRepository;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DonHangAdminController {
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    GiamGiaRepository giamGiaRepository;
    @Autowired
    NhanVienRepository nhanVienRepository;
    @RequestMapping("/admin/qldhdh")
    public String qldh(Model model) {
    model.addAttribute("hoaDon", hoaDonRepository.findAll());
    return "/admin/qldh";
}
@GetMapping("/admin/don-hang/huy/{id}")
    public String huy(@PathVariable Integer id, Model model) {
    HoaDon hd = hoaDonRepository.findById(id).get();
    hd.setTrangThai("Đã hủy");
    hoaDonRepository.save(hd);
        return "redirect:/admin/qldhdh";
}
@GetMapping("/admin/don-hang/chi-tiet/{id}")
    public String chiTiet(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("khang", khachHangRepository.findAll());
    model.addAttribute("nvien", nhanVienRepository.findAll());
    model.addAttribute("ggia", giamGiaRepository.findAll());
        model.addAttribute("hoadon", hoaDonRepository.findById(id).get());
        return "/admin/sdh";
    }
@PostMapping("/admin/sdh/{id}")
    public String sdh(@PathVariable("id") Integer id, HoaDon hoaDon) {
        hoaDon.setId(id);
        hoaDonRepository.save(hoaDon);
        return "redirect:/admin/qldhdh";
}
}
