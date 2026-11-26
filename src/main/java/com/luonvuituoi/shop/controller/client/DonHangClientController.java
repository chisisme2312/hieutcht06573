package com.luonvuituoi.shop.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DonHangClientController {

    @PostMapping("/dat-hang/succeed")
    public String successPost() {
        return "redirect:/don-hang-cua-toi";
    }

    @GetMapping("/tra-cuu-don-hang")
    public String traCuuDonHang() {
        return "redirect:/don-hang-cua-toi";
    }
}
