package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DonHangAdminController {
    @Autowired
    HoaDonRepository hoaDonRepository;
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
}
