package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.NhanVien;
import com.luonvuituoi.shop.entity.VaiTro;
import com.luonvuituoi.shop.repository.NhanVienRepository;
import com.luonvuituoi.shop.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/nhan-vien")
@RequiredArgsConstructor
@Slf4j
public class NhanVienAdminController {

    private final NhanVienRepository nhanVienRepository;
    private final VaiTroRepository vaiTroRepository;

    // 1. Danh sách nhân viên
    @GetMapping
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<NhanVien> list;
        if (keyword != null && !keyword.isBlank()) {
            list = nhanVienRepository.findByHoVaTenContainingIgnoreCaseOrTenDangNhapContainingIgnoreCaseOrSoDienThoaiContainingIgnoreCaseOrderByNgayTaoDesc(keyword.trim(), keyword.trim(), keyword.trim());
            model.addAttribute("keyword", keyword.trim());
        } else {
            list = nhanVienRepository.findAllByOrderByNgayTaoDesc();
        }

        model.addAttribute("listNhanVien", list);
        return "admin/nhan-vien/index";
    }

    // 2. Form thêm mới nhân viên
    @GetMapping("/them-moi")
    public String themMoi(Model model) {
        NhanVien nv = new NhanVien();
        nv.setTrangThai(true);
        nv.setGioiTinh(true);

        model.addAttribute("nhanVien", nv);
        model.addAttribute("listVaiTro", vaiTroRepository.findAll());
        model.addAttribute("isEdit", false);
        return "admin/nhan-vien/form";
    }

    // 3. Form chỉnh sửa nhân viên
    @GetMapping("/chinh-sua/{id}")
    public String chinhSua(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        var opt = nhanVienRepository.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy nhân viên!");
            return "redirect:/admin/nhan-vien";
        }

        model.addAttribute("nhanVien", opt.get());
        model.addAttribute("listVaiTro", vaiTroRepository.findAll());
        model.addAttribute("isEdit", true);
        return "admin/nhan-vien/form";
    }

    // 4. Lưu thông tin nhân viên
    @PostMapping("/luu")
    @Transactional
    public String luu(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("maNhanVien") String maNhanVien,
            @RequestParam("hoVaTen") String hoVaTen,
            @RequestParam("tenDangNhap") String tenDangNhap,
            @RequestParam(value = "matKhau", required = false) String matKhau,
            @RequestParam("idVaiTro") Integer idVaiTro,
            @RequestParam(value = "gioiTinh", required = false) Boolean gioiTinh,
            @RequestParam(value = "ngaySinh", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngaySinh,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "soDienThoai", required = false) String soDienThoai,
            @RequestParam(value = "diaChi", required = false) String diaChi,
            @RequestParam(value = "trangThai", defaultValue = "false") Boolean trangThai,
            RedirectAttributes ra) {

        String maTrim = maNhanVien.trim().toUpperCase();
        String usernameTrim = tenDangNhap.trim().toLowerCase();

        VaiTro vaiTro = vaiTroRepository.findById(idVaiTro).orElse(null);
        if (vaiTro == null) {
            ra.addFlashAttribute("errorMsg", "Vui lòng chọn vai trò hợp lệ!");
            return id != null ? "redirect:/admin/nhan-vien/chinh-sua/" + id : "redirect:/admin/nhan-vien/them-moi";
        }

        NhanVien nv;
        if (id != null) {
            nv = nhanVienRepository.findById(id).orElse(new NhanVien());
            if (matKhau != null && !matKhau.isBlank()) {
                nv.setMatKhau(matKhau.trim());
            }
        } else {
            // Check trùng mã hoặc username
            if (nhanVienRepository.findByMaNhanVien(maTrim).isPresent()) {
                ra.addFlashAttribute("errorMsg", "Mã nhân viên [" + maTrim + "] đã tồn tại!");
                return "redirect:/admin/nhan-vien/them-moi";
            }
            if (nhanVienRepository.findByTenDangNhap(usernameTrim).isPresent()) {
                ra.addFlashAttribute("errorMsg", "Tên đăng nhập [" + usernameTrim + "] đã được sử dụng!");
                return "redirect:/admin/nhan-vien/them-moi";
            }
            if (matKhau == null || matKhau.isBlank()) {
                ra.addFlashAttribute("errorMsg", "Vui lòng nhập mật khẩu cho nhân viên mới!");
                return "redirect:/admin/nhan-vien/them-moi";
            }

            nv = new NhanVien();
            nv.setMatKhau(matKhau.trim());
            nv.setNgayTao(LocalDateTime.now());
        }

        nv.setMaNhanVien(maTrim);
        nv.setHoVaTen(hoVaTen.trim());
        nv.setTenDangNhap(usernameTrim);
        nv.setVaiTro(vaiTro);
        nv.setGioiTinh(gioiTinh != null ? gioiTinh : true);
        nv.setNgaySinh(ngaySinh);
        nv.setEmail(email != null && !email.isBlank() ? email.trim() : null);
        nv.setSoDienThoai(soDienThoai != null && !soDienThoai.isBlank() ? soDienThoai.trim() : null);
        nv.setDiaChi(diaChi != null && !diaChi.isBlank() ? diaChi.trim() : null);
        nv.setTrangThai(trangThai);

        nhanVienRepository.save(nv);
        ra.addFlashAttribute("successMsg", "Đã lưu thông tin nhân viên [" + nv.getHoVaTen() + "] thành công!");
        return "redirect:/admin/nhan-vien";
    }

    // 5. Khóa / Mở khóa tài khoản nhân viên
    @PostMapping("/doi-trang-thai/{id}")
    @Transactional
    public String doiTrangThai(@PathVariable("id") Integer id, RedirectAttributes ra) {
        var opt = nhanVienRepository.findById(id);
        if (opt.isPresent()) {
            NhanVien nv = opt.get();
            boolean newStatus = !Boolean.TRUE.equals(nv.getTrangThai());
            nv.setTrangThai(newStatus);
            nhanVienRepository.save(nv);
            ra.addFlashAttribute("successMsg", "Đã " + (newStatus ? "kích hoạt hoạt động" : "tạm khóa") + " tài khoản nhân viên [" + nv.getHoVaTen() + "]!");
        }
        return "redirect:/admin/nhan-vien";
    }
}
