package com.luonvuituoi.shop.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DonHangClientController {
@PostMapping("/dat-hang/succeed")
    public String success() {
    return "/client/dhangcuatoi";
}
}
