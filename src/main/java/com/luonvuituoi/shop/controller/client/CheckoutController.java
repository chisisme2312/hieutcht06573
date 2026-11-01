package com.luonvuituoi.shop.controller.client;

import com.luonvuituoi.shop.repository.Dcrepository;
import com.luonvuituoi.shop.repository.GiamGiaRepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CheckoutController {
@Autowired
    KhachHangRepository khachHangRepository;
@Autowired
Dcrepository dcRepository;
@Autowired
GiamGiaRepository giamGiaRepository;
@RequestMapping("/dat-hang")
    public String gioHang(Model model) {
        model.addAttribute("khachHang",khachHangRepository.findAll());
        model.addAttribute("dclp",dcRepository.findAll());
        model.addAttribute("giamgia",giamGiaRepository.findAll());
        return "/client/checkout";
}
}
