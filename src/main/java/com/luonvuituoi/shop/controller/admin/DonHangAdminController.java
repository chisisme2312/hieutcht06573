package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.repository.GiamGiaRepository;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.repository.NhanVienRepository;
import com.luonvuituoi.shop.repository.SanPhamChiTietRepository;
import com.luonvuituoi.shop.repository.HoaDonChiTietRepository;
import com.luonvuituoi.shop.entity.HoaDonChiTiet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DonHangAdminController {

    private final HoaDonRepository hoaDonRepository;
    private final KhachHangRepository khachHangRepository;
    private final GiamGiaRepository giamGiaRepository;
    private final NhanVienRepository nhanVienRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    @GetMapping({"/admin/qldhdh", "/admin/don-hang"})
    public String qldh(@RequestParam(value = "trangThai", required = false) String trangThai, Model model) {
        List<HoaDon> allOrders = hoaDonRepository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getNgayTao() == null) return 1;
                    if (b.getNgayTao() == null) return -1;
                    return b.getNgayTao().compareTo(a.getNgayTao());
                }).toList();

        // Đếm số lượng theo trạng thái
        long tongTatCa = allOrders.size();
        long soChoXacNhan = allOrders.stream().filter(h -> "Chờ xác nhận".equalsIgnoreCase(h.getTrangThai())).count();
        long soDaXacNhan = allOrders.stream().filter(h -> "Đã xác nhận".equalsIgnoreCase(h.getTrangThai())).count();
        long soDangGiao = allOrders.stream().filter(h -> "Đang giao hàng".equalsIgnoreCase(h.getTrangThai())).count();
        long soHoanThanh = allOrders.stream().filter(h -> "Đã hoàn thành".equalsIgnoreCase(h.getTrangThai())).count();
        long soDaHuy = allOrders.stream().filter(h -> "Đã hủy".equalsIgnoreCase(h.getTrangThai())).count();

        // Lọc danh sách theo tab người dùng chọn
        List<HoaDon> filteredOrders = allOrders;
        if (trangThai != null && !trangThai.isBlank()) {
            filteredOrders = allOrders.stream()
                    .filter(h -> trangThai.equalsIgnoreCase(h.getTrangThai()))
                    .toList();
        }

        model.addAttribute("hoaDon", filteredOrders);
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("tongTatCa", tongTatCa);
        model.addAttribute("soChoXacNhan", soChoXacNhan);
        model.addAttribute("soDaXacNhan", soDaXacNhan);
        model.addAttribute("soDangGiao", soDangGiao);
        model.addAttribute("soHoanThanh", soHoanThanh);
        model.addAttribute("soDaHuy", soDaHuy);

        return "admin/qldh";
    }

    @PostMapping("/admin/don-hang/cap-nhat-trang-thai")
    @Transactional
    public String capNhatTrangThai(@RequestParam("id") Integer id, @RequestParam("trangThaiMoi") String trangThaiMoi, RedirectAttributes ra) {
        var hdOpt = hoaDonRepository.findById(id);
        if (hdOpt.isPresent()) {
            HoaDon hd = hdOpt.get();
            String trangThaiCu = hd.getTrangThai();

            // Nếu chuyển sang Đã hủy: Hoàn lại số lượng tồn kho tự động
            if ("Đã hủy".equalsIgnoreCase(trangThaiMoi) && !"Đã hủy".equalsIgnoreCase(trangThaiCu)) {
                List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(hd.getId());
                for (HoaDonChiTiet item : items) {
                    if (item.getSanPhamChiTiet() != null) {
                        sanPhamChiTietRepository.hoanTonKhoAtomic(item.getSanPhamChiTiet().getId(), item.getSoLuong());
                        sanPhamChiTietRepository.capNhatConHang(item.getSanPhamChiTiet().getId());
                    }
                }
            }

            hd.setTrangThai(trangThaiMoi);
            hoaDonRepository.save(hd);
            ra.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng [" + hd.getMaHoaDon() + "] thành [" + trangThaiMoi + "] thành công!");
        }
        return "redirect:/admin/qldhdh";
    }

    @GetMapping("/admin/don-hang/huy/{id}")
    @Transactional
    public String huy(@PathVariable Integer id, RedirectAttributes ra) {
        var hdOpt = hoaDonRepository.findById(id);
        if (hdOpt.isPresent()) {
            HoaDon hd = hdOpt.get();
            if (!"Đã hủy".equalsIgnoreCase(hd.getTrangThai())) {
                List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDonId(hd.getId());
                for (HoaDonChiTiet item : items) {
                    if (item.getSanPhamChiTiet() != null) {
                        sanPhamChiTietRepository.hoanTonKhoAtomic(item.getSanPhamChiTiet().getId(), item.getSoLuong());
                        sanPhamChiTietRepository.capNhatConHang(item.getSanPhamChiTiet().getId());
                    }
                }
                hd.setTrangThai("Đã hủy");
                hoaDonRepository.save(hd);
                ra.addFlashAttribute("success", "Đã hủy đơn hàng [" + hd.getMaHoaDon() + "] và tự động hoàn trả số lượng vào kho!");
            }
        }
        return "redirect:/admin/qldhdh";
    }

    @GetMapping("/admin/don-hang/chi-tiet/{id}")
    public String chiTiet(@PathVariable("id") Integer id) {
        // Chuyển hướng sang xem chi tiết hóa đơn chuẩn
        return "redirect:/admin/hoa-don/" + id;
    }
}
