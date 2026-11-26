package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.DiaChi;
import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.entity.KhachHang;
import com.luonvuituoi.shop.repository.Dcrepository;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/khach-hang")
@RequiredArgsConstructor
@Slf4j
public class KhachHangAdminController {

    private final KhachHangRepository khachHangRepository;
    private final HoaDonRepository hoaDonRepository;
    private final Dcrepository dcrepository;

    // 1. Danh sách khách hàng
    @GetMapping
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<KhachHang> list;
        if (keyword != null && !keyword.isBlank()) {
            list = khachHangRepository.findByHoTenContainingIgnoreCaseOrSoDienThoaiContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByNgayTaoDesc(keyword.trim(), keyword.trim(), keyword.trim());
            model.addAttribute("keyword", keyword.trim());
        } else {
            list = khachHangRepository.findAllByOrderByNgayTaoDesc();
        }

        model.addAttribute("listKhachHang", list);
        return "admin/khach-hang/index";
    }

    // 2. Chi tiết hồ sơ khách hàng & Lịch sử mua hàng & Sổ địa chỉ
    @GetMapping("/chi-tiet/{id}")
    public String chiTiet(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        var opt = khachHangRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy khách hàng!");
            return "redirect:/admin/khach-hang";
        }

        KhachHang kh = opt.get();
        List<DiaChi> diaChiList = dcrepository.findByKhachHangId(kh.getId());
        List<HoaDon> hoaDonList = hoaDonRepository.findByKhachHangIdOrderByNgayTaoDesc(kh.getId());

        model.addAttribute("khachHang", kh);
        model.addAttribute("diaChiList", diaChiList);
        model.addAttribute("hoaDonList", hoaDonList);
        return "admin/khach-hang/chi-tiet";
    }

    // 3. Khóa / Mở khóa tài khoản khách hàng
    @PostMapping("/doi-trang-thai/{id}")
    @Transactional
    public String doiTrangThai(@PathVariable("id") Integer id, RedirectAttributes ra) {
        var opt = khachHangRepository.findById(id);
        if (opt.isPresent()) {
            KhachHang kh = opt.get();
            boolean newStatus = !Boolean.TRUE.equals(kh.getTrangThai());
            kh.setTrangThai(newStatus);
            khachHangRepository.save(kh);
            ra.addFlashAttribute("successMsg", "Đã " + (newStatus ? "mở khóa hoạt động" : "tạm khóa") + " tài khoản khách hàng [" + kh.getHoTen() + "]!");
        }
        return "redirect:/admin/khach-hang";
    }
}
