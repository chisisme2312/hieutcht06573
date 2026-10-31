package com.luonvuituoi.shop.controller.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luonvuituoi.shop.dto.VariantDto;
import com.luonvuituoi.shop.entity.KichThuoc;
import com.luonvuituoi.shop.entity.MauSac;
import com.luonvuituoi.shop.entity.SanPham;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.service.SanPhamService;
import com.luonvuituoi.shop.service.ThuocTinhService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/san-pham")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final SanPhamService sanPhamService;
    private final ThuocTinhService thuocTinhService;
    private final ObjectMapper objectMapper;

    // 1. Danh mục sản phẩm (Bộ lọc đa năng & Tìm kiếm & Phân trang)
    @GetMapping
    public String danhSachSanPham(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "loaiSanPhamId", required = false) Integer loaiSanPhamId,
            @RequestParam(value = "mauSacId", required = false) Integer mauSacId,
            @RequestParam(value = "kichThuocId", required = false) Integer kichThuocId,
            @RequestParam(value = "mucGia", required = false) String mucGia,
            @RequestParam(value = "sort", defaultValue = "moi-nhat") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model) {

        // Xử lý khoảng giá
        BigDecimal minGia = null;
        BigDecimal maxGia = null;

        if (mucGia != null && !mucGia.isBlank()) {
            switch (mucGia) {
                case "duoi-200k" -> {
                    minGia = BigDecimal.ZERO;
                    maxGia = BigDecimal.valueOf(200000);
                }
                case "200k-350k" -> {
                    minGia = BigDecimal.valueOf(200000);
                    maxGia = BigDecimal.valueOf(350000);
                }
                case "350k-500k" -> {
                    minGia = BigDecimal.valueOf(350000);
                    maxGia = BigDecimal.valueOf(500000);
                }
                case "tren-500k" -> {
                    minGia = BigDecimal.valueOf(500000);
                    maxGia = null;
                }
            }
        }

        Page<SanPham> pageSanPham = sanPhamService.filterClientSanPham(
                keyword, loaiSanPhamId, mauSacId, kichThuocId, minGia, maxGia, sort, page, size);

        // Đổ dữ liệu bộ lọc
        model.addAttribute("pageSanPham", pageSanPham);
        model.addAttribute("danhSachSanPham", pageSanPham.getContent());
        model.addAttribute("danhSachLoai", thuocTinhService.getAllLoaiSanPham());
        model.addAttribute("danhSachMauSac", thuocTinhService.getAllMauSac());
        model.addAttribute("danhSachKichThuoc", thuocTinhService.getAllKichThuoc());

        // Giữ lại trạng thái lọc của người dùng
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedLoaiId", loaiSanPhamId);
        model.addAttribute("selectedMauSacId", mauSacId);
        model.addAttribute("selectedKichThuocId", kichThuocId);
        model.addAttribute("selectedMucGia", mucGia);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSanPham.getTotalPages());
        model.addAttribute("totalElements", pageSanPham.getTotalElements());

        return "client/san-pham";
    }

    // 2. Chi tiết sản phẩm (Album ảnh, chọn màu/size, tồn kho biến thể)
    @GetMapping("/{id}")
    public String chiTietSanPham(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Optional<SanPham> sanPhamOpt = sanPhamService.getByIdWithDetails(id);

        if (sanPhamOpt.isEmpty()) {
            ra.addFlashAttribute("thongBaoLoi", "Không tìm thấy sản phẩm hoặc sản phẩm đã ngừng kinh doanh!");
            return "redirect:/san-pham";
        }

        SanPham sanPham = sanPhamOpt.get();

        // Lọc danh sách biến thể còn hoạt động
        List<SanPhamChiTiet> listChiTiet = sanPham.getDanhSachChiTiet() != null
                ? sanPham.getDanhSachChiTiet().stream()
                .filter(ct -> Boolean.TRUE.equals(ct.getTrangThai()))
                .sorted(Comparator.comparing(SanPhamChiTiet::getId))
                .collect(Collectors.toList())
                : Collections.emptyList();

        // Lấy danh sách màu sắc và kích thước duy nhất có trong sản phẩm này
        Map<Integer, MauSac> mauSacMap = new LinkedHashMap<>();
        Map<Integer, KichThuoc> kichThuocMap = new LinkedHashMap<>();

        List<VariantDto> variantDtos = new ArrayList<>();

        for (SanPhamChiTiet ct : listChiTiet) {
            if (ct.getMauSac() != null) {
                mauSacMap.putIfAbsent(ct.getMauSac().getId(), ct.getMauSac());
            }
            if (ct.getKichThuoc() != null) {
                kichThuocMap.putIfAbsent(ct.getKichThuoc().getId(), ct.getKichThuoc());
            }

            variantDtos.add(VariantDto.builder()
                    .id(ct.getId())
                    .maSanPhamChiTiet(ct.getMaSanPhamChiTiet())
                    .mauSacId(ct.getMauSac() != null ? ct.getMauSac().getId() : null)
                    .tenMauSac(ct.getMauSac() != null ? ct.getMauSac().getTenMauSac() : "")
                    .kichThuocId(ct.getKichThuoc() != null ? ct.getKichThuoc().getId() : null)
                    .tenKichThuoc(ct.getKichThuoc() != null ? ct.getKichThuoc().getTenKichThuoc() : "")
                    .giaBan(ct.getGiaBan())
                    .giaBanBanDau(ct.getGiaBanBanDau())
                    .soLuongTon(ct.getSoLuongTon() != null ? ct.getSoLuongTon() : 0)
                    .trangThai(ct.getTrangThai())
                    .build());
        }

        // Serialized sang JSON để Frontend JavaScript tra cứu biến thể tức thì
        String variantsJson = "[]";
        try {
            variantsJson = objectMapper.writeValueAsString(variantDtos);
        } catch (JsonProcessingException e) {
            log.error("Lỗi parse variants sang json", e);
        }

        // Tính toán tổng tồn kho
        int tongTonKho = listChiTiet.stream()
                .mapToInt(ct -> ct.getSoLuongTon() != null ? ct.getSoLuongTon() : 0)
                .sum();

        // Lấy sản phẩm liên quan (cùng loại sản phẩm)
        Integer loaiId = sanPham.getLoaiSanPham() != null ? sanPham.getLoaiSanPham().getId() : null;
        List<SanPham> sanPhamLienQuan = sanPhamService.getSanPhamLienQuan(loaiId, sanPham.getId(), 4);

        model.addAttribute("sp", sanPham);
        model.addAttribute("listAnh", sanPham.getDanhSachAnh());
        model.addAttribute("listMauSac", new ArrayList<>(mauSacMap.values()));
        model.addAttribute("listKichThuoc", new ArrayList<>(kichThuocMap.values()));
        model.addAttribute("listChiTiet", listChiTiet);
        model.addAttribute("variantsJson", variantsJson);
        model.addAttribute("tongTonKho", tongTonKho);
        model.addAttribute("sanPhamLienQuan", sanPhamLienQuan);

        return "client/chi-tiet-san-pham";
    }
}
