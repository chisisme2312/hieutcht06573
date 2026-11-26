package com.luonvuituoi.shop.controller.client;

import com.luonvuituoi.shop.dto.AuthUser;
import com.luonvuituoi.shop.entity.DiaChi;
import com.luonvuituoi.shop.entity.KhachHang;
import com.luonvuituoi.shop.repository.Dcrepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tai-khoan")
@RequiredArgsConstructor
@Slf4j
public class TaiKhoanClientController {

    private final KhachHangRepository khachHangRepository;
    private final Dcrepository dcrepository;

    // 1. Giao diện Hồ sơ & Tùy chỉnh Địa chỉ của khách hàng
    @GetMapping
    public String trangTaiKhoan(HttpSession session, Model model, RedirectAttributes ra) {
        AuthUser currentUser = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (currentUser == null) {
            ra.addFlashAttribute("errorMsg", "Vui lòng đăng nhập để xem thông tin tài khoản và quản lý địa chỉ!");
            return "redirect:/login?redirect=/tai-khoan";
        }

        KhachHang khachHang = khachHangRepository.findById(currentUser.getId()).orElse(null);
        if (khachHang == null) {
            ra.addFlashAttribute("thongBaoLoi", "Không tìm thấy thông tin khách hàng!");
            return "redirect:/";
        }

        List<DiaChi> danhSachDiaChi = dcrepository.findByKhachHangId(khachHang.getId());
        DiaChi diaChiMacDinh = danhSachDiaChi.stream()
                .filter(dc -> Boolean.TRUE.equals(dc.getMacDinh()))
                .findFirst()
                .orElse(danhSachDiaChi.isEmpty() ? null : danhSachDiaChi.get(0));

        model.addAttribute("khachHang", khachHang);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("danhSachDiaChi", danhSachDiaChi);
        model.addAttribute("diaChiMacDinh", diaChiMacDinh);

        return "client/tai-khoan";
    }

    // 2. Cập nhật Thông tin cá nhân (Họ tên, SĐT, Giới tính, Ngày sinh)
    @PostMapping("/thong-tin/cap-nhat")
    @Transactional
    public String capNhatThongTin(
            @RequestParam("hoTen") String hoTen,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "gioiTinh", required = false) Boolean gioiTinh,
            @RequestParam(value = "ngaySinh", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngaySinh,
            HttpSession session,
            RedirectAttributes ra) {

        AuthUser currentUser = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (currentUser == null) {
            return "redirect:/login";
        }

        KhachHang kh = khachHangRepository.findById(currentUser.getId()).orElse(null);
        if (kh == null) {
            ra.addFlashAttribute("thongBaoLoi", "Không tìm thấy khách hàng!");
            return "redirect:/tai-khoan";
        }

        kh.setHoTen(hoTen.trim());
        kh.setSoDienThoai(soDienThoai.trim());
        if (email != null && !email.isBlank()) kh.setEmail(email.trim());
        kh.setGioiTinh(gioiTinh != null ? gioiTinh : true);
        kh.setNgaySinh(ngaySinh);
        khachHangRepository.save(kh);

        // Cập nhật session
        currentUser.setHoTen(kh.getHoTen());
        currentUser.setSoDienThoai(kh.getSoDienThoai());
        currentUser.setEmail(kh.getEmail());
        session.setAttribute(AuthServiceImpl.SESSION_USER_KEY, currentUser);

        ra.addFlashAttribute("thongBaoThanhCong", "Cập nhật hồ sơ tài khoản thành công!");
        return "redirect:/tai-khoan";
    }

    // 3. Tùy chỉnh / Cập nhật Địa chỉ nhận hàng (Phong cách Shopee)
    @PostMapping("/dia-chi/luu")
    @Transactional
    public String luuDiaChi(
            @RequestParam(value = "diaChiId", required = false) Integer diaChiId,
            @RequestParam("duong") String duong,
            @RequestParam(value = "xaPhuong", required = false) String xaPhuong,
            @RequestParam(value = "quanHuyen", required = false) String quanHuyen,
            @RequestParam(value = "tinhThanhPho", required = false) String tinhThanhPho,
            @RequestParam(value = "macDinh", defaultValue = "false") Boolean macDinh,
            HttpSession session,
            RedirectAttributes ra) {

        AuthUser currentUser = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (currentUser == null) {
            return "redirect:/login";
        }

        KhachHang kh = khachHangRepository.findById(currentUser.getId()).orElse(null);
        if (kh == null) {
            ra.addFlashAttribute("thongBaoLoi", "Không tìm thấy khách hàng!");
            return "redirect:/tai-khoan";
        }

        DiaChi dc;
        if (diaChiId != null) {
            dc = dcrepository.findById(diaChiId).orElse(new DiaChi());
        } else {
            dc = new DiaChi();
        }

        dc.setKhachHang(kh);
        dc.setDuong(duong.trim());
        if (xaPhuong != null) dc.setXaPhuong(xaPhuong.trim());
        if (quanHuyen != null) dc.setQuanHuyen(quanHuyen.trim());
        if (tinhThanhPho != null) dc.setTinhThanhPho(tinhThanhPho.trim());

        // Nếu đặt làm mặc định hoặc là địa chỉ duy nhất
        List<DiaChi> currentList = dcrepository.findByKhachHangId(kh.getId());
        if (macDinh || currentList.isEmpty()) {
            for (DiaChi other : currentList) {
                if (other.getId() != null && !other.getId().equals(dc.getId())) {
                    other.setMacDinh(false);
                    dcrepository.save(other);
                }
            }
            dc.setMacDinh(true);
        } else {
            dc.setMacDinh(false);
        }

        dcrepository.save(dc);
        ra.addFlashAttribute("thongBaoThanhCong", "Đã lưu địa chỉ nhận hàng thành công!");
        return "redirect:/tai-khoan?tab=dia-chi";
    }

    // 4. Xóa địa chỉ nhận hàng
    @PostMapping("/dia-chi/xoa/{id}")
    @Transactional
    public String xoaDiaChi(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes ra) {
        AuthUser currentUser = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (currentUser == null) return "redirect:/login";

        var dcOpt = dcrepository.findById(id);
        if (dcOpt.isPresent() && dcOpt.get().getKhachHang().getId().equals(currentUser.getId())) {
            dcrepository.delete(dcOpt.get());
            ra.addFlashAttribute("thongBaoThanhCong", "Đã xóa địa chỉ thành công!");
        }
        return "redirect:/tai-khoan?tab=dia-chi";
    }

    // 5. Đổi Mật Khẩu
    @PostMapping("/doi-mat-khau")
    @Transactional
    public String doiMatKhau(
            @RequestParam("matKhauHienTai") String matKhauHienTai,
            @RequestParam("matKhauMoi") String matKhauMoi,
            @RequestParam("xacNhanMatKhau") String xacNhanMatKhau,
            HttpSession session,
            RedirectAttributes ra) {

        AuthUser currentUser = (AuthUser) session.getAttribute(AuthServiceImpl.SESSION_USER_KEY);
        if (currentUser == null) return "redirect:/login";

        KhachHang kh = khachHangRepository.findById(currentUser.getId()).orElse(null);
        if (kh == null) return "redirect:/login";

        if (!matKhauHienTai.equals(kh.getMatKhau())) {
            ra.addFlashAttribute("thongBaoLoi", "Mật khẩu hiện tại không chính xác!");
            return "redirect:/tai-khoan?tab=mat-khau";
        }

        if (matKhauMoi.length() < 4) {
            ra.addFlashAttribute("thongBaoLoi", "Mật khẩu mới phải có ít nhất 4 ký tự!");
            return "redirect:/tai-khoan?tab=mat-khau";
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            ra.addFlashAttribute("thongBaoLoi", "Mật khẩu xác nhận không khớp!");
            return "redirect:/tai-khoan?tab=mat-khau";
        }

        kh.setMatKhau(matKhauMoi);
        khachHangRepository.save(kh);
        ra.addFlashAttribute("thongBaoThanhCong", "Đổi mật khẩu thành công!");
        return "redirect:/tai-khoan?tab=mat-khau";
    }
}
