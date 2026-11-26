package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.GiamGia;
import com.luonvuituoi.shop.repository.GiamGiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/giam-gia")
@RequiredArgsConstructor
@Slf4j
public class GiamGiaAdminController {

    private final GiamGiaRepository giamGiaRepository;

    // 1. Danh sách mã giảm giá
    @GetMapping
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<GiamGia> list;
        if (keyword != null && !keyword.isBlank()) {
            list = giamGiaRepository.findByMaGiamGiaContainingIgnoreCaseOrTenGiamGiaContainingIgnoreCaseOrderByNgayTaoDesc(keyword.trim(), keyword.trim());
            model.addAttribute("keyword", keyword.trim());
        } else {
            list = giamGiaRepository.findAllByOrderByNgayTaoDesc();
        }

        model.addAttribute("listGiamGia", list);
        return "admin/giam-gia/index";
    }

    // 2. Form thêm mới
    @GetMapping("/them-moi")
    public String themMoi(Model model) {
        GiamGia gg = new GiamGia();
        gg.setTrangThai(true);
        gg.setLoaiGiamGia(1); // Mặc định %
        gg.setNgayBatDau(LocalDateTime.now());
        gg.setNgayKetThuc(LocalDateTime.now().plusMonths(1));
        gg.setSoLuong(100);

        model.addAttribute("giamGia", gg);
        model.addAttribute("isEdit", false);
        return "admin/giam-gia/form";
    }

    // 3. Form chỉnh sửa
    @GetMapping("/chinh-sua/{id}")
    public String chinhSua(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        var opt = giamGiaRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy mã giảm giá!");
            return "redirect:/admin/giam-gia";
        }

        model.addAttribute("giamGia", opt.get());
        model.addAttribute("isEdit", true);
        return "admin/giam-gia/form";
    }

    // 4. Lưu mã giảm giá
    @PostMapping("/luu")
    @Transactional
    public String luu(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("maGiamGia") String maGiamGia,
            @RequestParam("tenGiamGia") String tenGiamGia,
            @RequestParam("loaiGiamGia") Integer loaiGiamGia,
            @RequestParam("giaTriGiam") BigDecimal giaTriGiam,
            @RequestParam(value = "toiDaGiamGia", required = false) BigDecimal toiDaGiamGia,
            @RequestParam(value = "giaTriToiThieu", required = false) BigDecimal giaTriToiThieu,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("ngayBatDau") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayBatDau,
            @RequestParam("ngayKetThuc") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayKetThuc,
            @RequestParam(value = "trangThai", defaultValue = "false") Boolean trangThai,
            @RequestParam(value = "moTa", required = false) String moTa,
            RedirectAttributes ra) {

        String maTrim = maGiamGia.trim().toUpperCase();

        if (ngayKetThuc.isBefore(ngayBatDau)) {
            ra.addFlashAttribute("errorMsg", "Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
            return id != null ? "redirect:/admin/giam-gia/chinh-sua/" + id : "redirect:/admin/giam-gia/them-moi";
        }

        if (loaiGiamGia == 1 && (giaTriGiam.compareTo(BigDecimal.ZERO) <= 0 || giaTriGiam.compareTo(BigDecimal.valueOf(100)) > 0)) {
            ra.addFlashAttribute("errorMsg", "Giảm giá theo % phải nằm trong khoảng từ 1% đến 100%!");
            return id != null ? "redirect:/admin/giam-gia/chinh-sua/" + id : "redirect:/admin/giam-gia/them-moi";
        }

        GiamGia gg;
        if (id != null) {
            gg = giamGiaRepository.findById(id).orElse(new GiamGia());
        } else {
            // Kiểm tra trùng mã
            if (giamGiaRepository.findByMaGiamGia(maTrim).isPresent()) {
                ra.addFlashAttribute("errorMsg", "Mã giảm giá [" + maTrim + "] đã tồn tại! Vui lòng chọn mã khác.");
                return "redirect:/admin/giam-gia/them-moi";
            }
            gg = new GiamGia();
            gg.setNgayTao(LocalDateTime.now());
        }

        gg.setMaGiamGia(maTrim);
        gg.setTenGiamGia(tenGiamGia.trim());
        gg.setLoaiGiamGia(loaiGiamGia);
        gg.setGiaTriGiam(giaTriGiam);
        gg.setToiDaGiamGia(toiDaGiamGia != null ? toiDaGiamGia : BigDecimal.ZERO);
        gg.setGiaTriToiThieu(giaTriToiThieu != null ? giaTriToiThieu : BigDecimal.ZERO);
        gg.setSoLuong(soLuong);
        gg.setNgayBatDau(ngayBatDau);
        gg.setNgayKetThuc(ngayKetThuc);
        gg.setTrangThai(trangThai);
        gg.setMoTa(moTa != null ? moTa.trim() : null);

        giamGiaRepository.save(gg);
        ra.addFlashAttribute("successMsg", "Đã lưu mã giảm giá [" + gg.getMaGiamGia() + "] thành công!");
        return "redirect:/admin/giam-gia";
    }

    // 5. Chuyển đổi trạng thái (Bật/Tắt kích hoạt)
    @PostMapping("/doi-trang-thai/{id}")
    @Transactional
    public String doiTrangThai(@PathVariable("id") Integer id, RedirectAttributes ra) {
        var opt = giamGiaRepository.findById(id);
        if (opt.isPresent()) {
            GiamGia gg = opt.get();
            boolean newStatus = !Boolean.TRUE.equals(gg.getTrangThai());
            gg.setTrangThai(newStatus);
            giamGiaRepository.save(gg);
            ra.addFlashAttribute("successMsg", "Đã chuyển trạng thái mã [" + gg.getMaGiamGia() + "] sang " + (newStatus ? "Kích hoạt" : "Ngừng áp dụng") + "!");
        }
        return "redirect:/admin/giam-gia";
    }
}
