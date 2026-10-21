package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.Anh;
import com.luonvuituoi.shop.entity.SanPham;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.service.SanPhamChiTietService;
import com.luonvuituoi.shop.service.SanPhamService;
import com.luonvuituoi.shop.service.ThuocTinhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/san-pham")
@RequiredArgsConstructor
public class SanPhamAdminController {

    private final SanPhamService sanPhamService;
    private final SanPhamChiTietService sanPhamChiTietService;
    private final ThuocTinhService thuocTinhService;

    // 1. Danh sách sản phẩm
    @GetMapping
    public String danhSachSanPham(@RequestParam(value = "keyword", required = false) String keyword,
                                  Model model) {
        List<SanPham> danhSach;
        if (keyword != null && !keyword.trim().isEmpty()) {
            danhSach = sanPhamService.searchByName(keyword.trim());
            model.addAttribute("keyword", keyword.trim());
        } else {
            danhSach = sanPhamService.getAll();
        }
        model.addAttribute("danhSach", danhSach);
        return "admin/san-pham/index";
    }

    // 2. Form Thêm Mẫu Áo Mới
    @GetMapping("/them-moi")
    public String formThemMoi(Model model) {
        model.addAttribute("sanPham", new SanPham());
        model.addAttribute("isEdit", false);
        
        model.addAttribute("listMauSac", thuocTinhService.getAllMauSac());
        model.addAttribute("listKichThuoc", thuocTinhService.getAllKichThuoc());
        model.addAttribute("listChatLieu", thuocTinhService.getAllChatLieu());
        model.addAttribute("listLoaiSanPham", thuocTinhService.getAllLoaiSanPham());
        model.addAttribute("listNhaCungCap", thuocTinhService.getAllNhaCungCap());

        return "admin/san-pham/form";
    }

    // 3. Form Chỉnh Sửa Mẫu Áo
    @GetMapping("/chinh-sua/{id}")
    public String formChinhSua(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        var spOpt = sanPhamService.getById(id);
        if (spOpt.isEmpty()) {
            ra.addFlashAttribute("loi", "Không tìm thấy mẫu áo cần sửa!");
            return "redirect:/admin/san-pham";
        }

        model.addAttribute("sanPham", spOpt.get());
        model.addAttribute("isEdit", true);
        
        model.addAttribute("listMauSac", thuocTinhService.getAllMauSac());
        model.addAttribute("listKichThuoc", thuocTinhService.getAllKichThuoc());
        model.addAttribute("listChatLieu", thuocTinhService.getAllChatLieu());
        model.addAttribute("listLoaiSanPham", thuocTinhService.getAllLoaiSanPham());
        model.addAttribute("listNhaCungCap", thuocTinhService.getAllNhaCungCap());

        return "admin/san-pham/form";
    }

    // 4. Lưu / Cập nhật mẫu áo
    @PostMapping("/luu")
    public String luuSanPham(@ModelAttribute("sanPham") SanPham sanPham,
                             @RequestParam(value = "mauSacIds", required = false) List<Integer> mauSacIds,
                             @RequestParam(value = "kichThuocIds", required = false) List<Integer> kichThuocIds,
                             @RequestParam(value = "giaBan", required = false) BigDecimal giaBan,
                             @RequestParam(value = "giaBanBanDau", required = false) BigDecimal giaBanBanDau,
                             @RequestParam(value = "soLuongTon", required = false) Integer soLuongTon,
                             RedirectAttributes ra) {
        try {
            // Đảm bảo coAo luôn có giá trị hợp lệ để không bị lỗi NOT NULL trong CSDL
            if (sanPham.getCoAo() == null || sanPham.getCoAo().getId() == null) {
                var listCoAo = thuocTinhService.getAllCoAo();
                if (!listCoAo.isEmpty()) {
                    sanPham.setCoAo(listCoAo.get(0));
                }
            }

            if (sanPham.getId() != null) {
                var spCuOpt = sanPhamService.getById(sanPham.getId());
                if (spCuOpt.isPresent()) {
                    var spCu = spCuOpt.get();
                    spCu.setMaSanPham(sanPham.getMaSanPham());
                    spCu.setTenSanPham(sanPham.getTenSanPham());
                    spCu.setLoaiSanPham(sanPham.getLoaiSanPham());
                    spCu.setNhaCungCap(sanPham.getNhaCungCap());
                    spCu.setChatLieu(sanPham.getChatLieu());
                    spCu.setMoTa(sanPham.getMoTa());
                    spCu.setTrangThai(sanPham.getTrangThai());
                    if (sanPham.getCoAo() != null) {
                        spCu.setCoAo(sanPham.getCoAo());
                    }
                    sanPhamService.save(spCu);
                    ra.addFlashAttribute("thongBao", "Cập nhật thông tin mẫu áo thành công!");
                }
            } else {
                sanPhamService.saveWithVariants(sanPham, mauSacIds, kichThuocIds, giaBan, giaBanBanDau, soLuongTon);
                ra.addFlashAttribute("thongBao", "Thêm mẫu áo và tạo biến thể thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/san-pham";
    }

    // 5. Xóa mẫu áo
    @GetMapping("/xoa/{id}")
    public String xoaSanPham(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            sanPhamService.delete(id);
            ra.addFlashAttribute("thongBao", "Xóa mẫu áo thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể xóa do mẫu áo này đã có đơn hàng hoặc giỏ hàng liên kết!");
        }
        return "redirect:/admin/san-pham";
    }

    // 6. QUẢN LÝ BIẾN THỂ CHI TIẾT (SIZE, MÀU, KHO, GIÁ, ALBUM ẢNH)
    @GetMapping("/bien-the/{id}")
    public String quanLyBienThe(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        var spOpt = sanPhamService.getById(id);
        if (spOpt.isEmpty()) {
            ra.addFlashAttribute("loi", "Không tìm thấy mẫu áo!");
            return "redirect:/admin/san-pham";
        }

        SanPham sp = spOpt.get();
        model.addAttribute("sanPham", sp);
        model.addAttribute("listBienThe", sanPhamChiTietService.getBySanPhamId(id));
        model.addAttribute("listAnh", sanPhamChiTietService.getAnhBySanPhamId(id));

        model.addAttribute("listMauSac", thuocTinhService.getAllMauSac());
        model.addAttribute("listKichThuoc", thuocTinhService.getAllKichThuoc());
        model.addAttribute("bienTheMoi", new SanPhamChiTiet());
        model.addAttribute("anhMoi", new Anh());

        return "admin/san-pham/bien-the";
    }

    // 7. Thêm hoặc Sửa 1 Biến Thể Chi Tiết
    @PostMapping("/bien-the/luu")
    public String luuBienThe(@ModelAttribute("bienTheMoi") SanPhamChiTiet chiTiet,
                             @RequestParam("sanPhamId") Integer sanPhamId,
                             RedirectAttributes ra) {
        try {
            var spOpt = sanPhamService.getById(sanPhamId);
            if (spOpt.isPresent()) {
                chiTiet.setSanPham(spOpt.get());
                
                // Tránh lỗi null cột gia_ban_ban_dau
                if (chiTiet.getGiaBanBanDau() == null) {
                    chiTiet.setGiaBanBanDau(chiTiet.getGiaBan() != null ? chiTiet.getGiaBan() : BigDecimal.ZERO);
                }
                if (chiTiet.getGiaBan() == null) {
                    chiTiet.setGiaBan(BigDecimal.ZERO);
                }
                if (chiTiet.getSoLuongTon() == null) {
                    chiTiet.setSoLuongTon(10);
                }
                
                if (chiTiet.getMaSanPhamChiTiet() == null || chiTiet.getMaSanPhamChiTiet().trim().isEmpty()) {
                    chiTiet.setMaSanPhamChiTiet(spOpt.get().getMaSanPham() + "-" + chiTiet.getMauSac().getId() + "-" + chiTiet.getKichThuoc().getId());
                }
                sanPhamChiTietService.save(chiTiet);
                ra.addFlashAttribute("thongBao", "Lưu biến thể áo thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Lỗi lưu biến thể: " + e.getMessage());
        }
        return "redirect:/admin/san-pham/bien-the/" + sanPhamId;
    }

    // 8. Xóa Biến Thể Chi Tiết
    @GetMapping("/bien-the/xoa/{id}")
    public String xoaBienThe(@PathVariable("id") Integer id,
                             @RequestParam("sanPhamId") Integer sanPhamId,
                             RedirectAttributes ra) {
        try {
            sanPhamChiTietService.delete(id);
            ra.addFlashAttribute("thongBao", "Xóa biến thể thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể xóa biến thể này vì đã có dữ liệu hóa đơn!");
        }
        return "redirect:/admin/san-pham/bien-the/" + sanPhamId;
    }

    // 9. Thêm link ảnh cho mẫu áo
    @PostMapping("/anh/them")
    public String themAnh(@RequestParam("sanPhamId") Integer sanPhamId,
                          @RequestParam("anhUrl") String anhUrl,
                          RedirectAttributes ra) {
        try {
            var spOpt = sanPhamService.getById(sanPhamId);
            if (spOpt.isPresent() && anhUrl != null && !anhUrl.trim().isEmpty()) {
                Anh anh = Anh.builder()
                        .sanPham(spOpt.get())
                        .anhUrl(anhUrl.trim())
                        .trangThai(true)
                        .build();
                sanPhamChiTietService.saveAnh(anh);
                ra.addFlashAttribute("thongBao", "Thêm ảnh sản phẩm thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Lỗi thêm ảnh: " + e.getMessage());
        }
        return "redirect:/admin/san-pham/bien-the/" + sanPhamId;
    }

    // 10. Xóa ảnh
    @GetMapping("/anh/xoa/{id}")
    public String xoaAnh(@PathVariable("id") Integer id,
                         @RequestParam("sanPhamId") Integer sanPhamId,
                         RedirectAttributes ra) {
        sanPhamChiTietService.deleteAnh(id);
        ra.addFlashAttribute("thongBao", "Xóa ảnh thành công!");
        return "redirect:/admin/san-pham/bien-the/" + sanPhamId;
    }
}
