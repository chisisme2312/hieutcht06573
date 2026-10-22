package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.*;
import com.luonvuituoi.shop.service.ThuocTinhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/thuoc-tinh")
@RequiredArgsConstructor
public class ThuocTinhAdminController {

    private final ThuocTinhService thuocTinhService;

    // 1. MÀU SẮC
    @GetMapping("/mau-sac")
    public String danhSachMauSac(Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllMauSac());
        model.addAttribute("item", new MauSac());
        model.addAttribute("isEdit", false);
        model.addAttribute("tenThuocTinh", "Màu Sắc");
        model.addAttribute("duongDan", "mau-sac");
        return "admin/thuoc-tinh/mau-sac";
    }

    @PostMapping("/mau-sac/save")
    public String saveMauSac(@ModelAttribute("item") MauSac mauSac, RedirectAttributes ra) {
        try {
            thuocTinhService.saveMauSac(mauSac);
            ra.addFlashAttribute("thongBao", "Lưu màu sắc thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể lưu: Tên màu sắc có thể đã tồn tại!");
        }
        return "redirect:/admin/thuoc-tinh/mau-sac";
    }

    @GetMapping("/mau-sac/edit/{id}")
    public String editMauSac(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllMauSac());
        model.addAttribute("item", thuocTinhService.getMauSacById(id).orElse(new MauSac()));
        model.addAttribute("isEdit", true);
        model.addAttribute("tenThuocTinh", "Màu Sắc");
        model.addAttribute("duongDan", "mau-sac");
        return "admin/thuoc-tinh/mau-sac";
    }

    @GetMapping("/mau-sac/delete/{id}")
    public String deleteMauSac(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            thuocTinhService.deleteMauSac(id);
            ra.addFlashAttribute("thongBao", "Xóa màu sắc thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể xóa do thuộc tính này đang được sử dụng trong sản phẩm!");
        }
        return "redirect:/admin/thuoc-tinh/mau-sac";
    }

    // 2. KÍCH THƯỚC (SIZE)
    @GetMapping("/kich-thuoc")
    public String danhSachKichThuoc(Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllKichThuoc());
        model.addAttribute("item", new KichThuoc());
        model.addAttribute("isEdit", false);
        model.addAttribute("tenThuocTinh", "Kích Thước");
        model.addAttribute("duongDan", "kich-thuoc");
        return "admin/thuoc-tinh/kich-thuoc";
    }

    @PostMapping("/kich-thuoc/save")
    public String saveKichThuoc(@ModelAttribute("item") KichThuoc kichThuoc, RedirectAttributes ra) {
        try {
            thuocTinhService.saveKichThuoc(kichThuoc);
            ra.addFlashAttribute("thongBao", "Lưu kích thước thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể lưu: Tên kích thước có thể đã tồn tại!");
        }
        return "redirect:/admin/thuoc-tinh/kich-thuoc";
    }

    @GetMapping("/kich-thuoc/edit/{id}")
    public String editKichThuoc(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllKichThuoc());
        model.addAttribute("item", thuocTinhService.getKichThuocById(id).orElse(new KichThuoc()));
        model.addAttribute("isEdit", true);
        model.addAttribute("tenThuocTinh", "Kích Thước");
        model.addAttribute("duongDan", "kich-thuoc");
        return "admin/thuoc-tinh/kich-thuoc";
    }

    @GetMapping("/kich-thuoc/delete/{id}")
    public String deleteKichThuoc(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            thuocTinhService.deleteKichThuoc(id);
            ra.addFlashAttribute("thongBao", "Xóa kích thước thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể xóa do kích thước này đang được sử dụng!");
        }
        return "redirect:/admin/thuoc-tinh/kich-thuoc";
    }

    // 3. CHẤT LIỆU
    @GetMapping("/chat-lieu")
    public String danhSachChatLieu(Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllChatLieu());
        model.addAttribute("item", new ChatLieu());
        model.addAttribute("isEdit", false);
        model.addAttribute("tenThuocTinh", "Chất Liệu");
        model.addAttribute("duongDan", "chat-lieu");
        return "admin/thuoc-tinh/chat-lieu";
    }

    @PostMapping("/chat-lieu/save")
    public String saveChatLieu(@ModelAttribute("item") ChatLieu chatLieu, RedirectAttributes ra) {
        try {
            thuocTinhService.saveChatLieu(chatLieu);
            ra.addFlashAttribute("thongBao", "Lưu chất liệu thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể lưu: Tên chất liệu có thể đã tồn tại!");
        }
        return "redirect:/admin/thuoc-tinh/chat-lieu";
    }

    @GetMapping("/chat-lieu/edit/{id}")
    public String editChatLieu(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllChatLieu());
        model.addAttribute("item", thuocTinhService.getChatLieuById(id).orElse(new ChatLieu()));
        model.addAttribute("isEdit", true);
        model.addAttribute("tenThuocTinh", "Chất Liệu");
        model.addAttribute("duongDan", "chat-lieu");
        return "admin/thuoc-tinh/chat-lieu";
    }

    @GetMapping("/chat-lieu/delete/{id}")
    public String deleteChatLieu(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            thuocTinhService.deleteChatLieu(id);
            ra.addFlashAttribute("thongBao", "Xóa chất liệu thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể xóa do chất liệu này đang được sử dụng!");
        }
        return "redirect:/admin/thuoc-tinh/chat-lieu";
    }

    // 4. LOẠI SẢN PHẨM
    @GetMapping("/loai-san-pham")
    public String danhSachLoaiSanPham(Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllLoaiSanPham());
        model.addAttribute("item", new LoaiSanPham());
        model.addAttribute("isEdit", false);
        model.addAttribute("tenThuocTinh", "Loại Sản Phẩm");
        model.addAttribute("duongDan", "loai-san-pham");
        return "admin/thuoc-tinh/loai-san-pham";
    }

    @PostMapping("/loai-san-pham/save")
    public String saveLoaiSanPham(@ModelAttribute("item") LoaiSanPham loaiSanPham, RedirectAttributes ra) {
        try {
            thuocTinhService.saveLoaiSanPham(loaiSanPham);
            ra.addFlashAttribute("thongBao", "Lưu loại sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể lưu: Tên loại sản phẩm có thể đã tồn tại!");
        }
        return "redirect:/admin/thuoc-tinh/loai-san-pham";
    }

    @GetMapping("/loai-san-pham/edit/{id}")
    public String editLoaiSanPham(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllLoaiSanPham());
        model.addAttribute("item", thuocTinhService.getLoaiSanPhamById(id).orElse(new LoaiSanPham()));
        model.addAttribute("isEdit", true);
        model.addAttribute("tenThuocTinh", "Loại Sản Phẩm");
        model.addAttribute("duongDan", "loai-san-pham");
        return "admin/thuoc-tinh/loai-san-pham";
    }

    @GetMapping("/loai-san-pham/delete/{id}")
    public String deleteLoaiSanPham(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            thuocTinhService.deleteLoaiSanPham(id);
            ra.addFlashAttribute("thongBao", "Xóa loại sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể xóa do loại sản phẩm này đang được sử dụng!");
        }
        return "redirect:/admin/thuoc-tinh/loai-san-pham";
    }

    // 5. NHÀ CUNG CẤP
    @GetMapping("/nha-cung-cap")
    public String danhSachNhaCungCap(Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllNhaCungCap());
        model.addAttribute("item", new NhaCungCap());
        model.addAttribute("isEdit", false);
        model.addAttribute("tenThuocTinh", "Nhà Cung Cấp / Thương Hiệu");
        model.addAttribute("duongDan", "nha-cung-cap");
        return "admin/thuoc-tinh/nha-cung-cap";
    }

    @PostMapping("/nha-cung-cap/save")
    public String saveNhaCungCap(@ModelAttribute("item") NhaCungCap nhaCungCap, RedirectAttributes ra) {
        try {
            thuocTinhService.saveNhaCungCap(nhaCungCap);
            ra.addFlashAttribute("thongBao", "Lưu nhà cung cấp thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể lưu: Tên nhà cung cấp có thể đã tồn tại!");
        }
        return "redirect:/admin/thuoc-tinh/nha-cung-cap";
    }

    @GetMapping("/nha-cung-cap/edit/{id}")
    public String editNhaCungCap(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("danhSach", thuocTinhService.getAllNhaCungCap());
        model.addAttribute("item", thuocTinhService.getNhaCungCapById(id).orElse(new NhaCungCap()));
        model.addAttribute("isEdit", true);
        model.addAttribute("tenThuocTinh", "Nhà Cung Cấp / Thương Hiệu");
        model.addAttribute("duongDan", "nha-cung-cap");
        return "admin/thuoc-tinh/nha-cung-cap";
    }

    @GetMapping("/nha-cung-cap/delete/{id}")
    public String deleteNhaCungCap(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            thuocTinhService.deleteNhaCungCap(id);
            ra.addFlashAttribute("thongBao", "Xóa nhà cung cấp thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("loi", "Không thể xóa do nhà cung cấp này đang được sử dụng!");
        }
        return "redirect:/admin/thuoc-tinh/nha-cung-cap";
    }
}
