package com.luonvuituoi.shop.controller.client;

import com.luonvuituoi.shop.dto.CartDto;
import com.luonvuituoi.shop.service.GioHangService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/gio-hang")
@RequiredArgsConstructor
@Slf4j
public class GioHangController {

    private final GioHangService gioHangService;

    // 1. Hiển thị trang giỏ hàng
    @GetMapping
    public String xemGioHang(HttpSession session, Model model) {
        CartDto cart = gioHangService.getGioHang(session);
        model.addAttribute("cart", cart);
        return "client/gio-hang";
    }

    // 2. Thêm vào giỏ hàng (Hỗ trợ cả Form submit lẫn AJAX)
    @PostMapping("/them")
    public Object themVaoGio(@RequestParam("sanPhamChiTietId") Integer sanPhamChiTietId,
                             @RequestParam(value = "soLuong", defaultValue = "1") Integer soLuong,
                             HttpSession session,
                             HttpServletRequest request,
                             RedirectAttributes ra) {
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        try {
            CartDto cart = gioHangService.themSanPham(session, sanPhamChiTietId, soLuong);

            if (isAjax) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("message", "Đã thêm sản phẩm vào giỏ hàng thành công!");
                resp.put("totalCount", cart.getTongSoLuong());
                resp.put("tamTinh", cart.getTamTinh());
                resp.put("tongTien", cart.getTongTien());
                return ResponseEntity.ok(resp);
            }

            ra.addFlashAttribute("thongBaoThanhCong", "Đã thêm sản phẩm vào giỏ hàng!");
            return "redirect:/gio-hang";

        } catch (IllegalArgumentException ex) {
            log.warn("Lỗi thêm vào giỏ: {}", ex.getMessage());
            if (isAjax) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", ex.getMessage());
                return ResponseEntity.badRequest().body(resp);
            }
            ra.addFlashAttribute("thongBaoLoi", ex.getMessage());
            return "redirect:/gio-hang";
        } catch (Exception ex) {
            log.error("Lỗi không xác định khi thêm vào giỏ", ex);
            if (isAjax) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", "Đã có lỗi xảy ra khi thêm vào giỏ hàng!");
                return ResponseEntity.internalServerError().body(resp);
            }
            ra.addFlashAttribute("thongBaoLoi", "Đã có lỗi xảy ra khi thêm vào giỏ hàng!");
            return "redirect:/gio-hang";
        }
    }

    // 3. Cập nhật số lượng sản phẩm trong giỏ
    @PostMapping("/cap-nhat")
    public Object capNhatSoLuong(@RequestParam("sanPhamChiTietId") Integer sanPhamChiTietId,
                                @RequestParam("soLuong") Integer soLuong,
                                HttpSession session,
                                HttpServletRequest request,
                                RedirectAttributes ra) {
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        try {
            CartDto cart = gioHangService.capNhatSoLuong(session, sanPhamChiTietId, soLuong);

            if (isAjax) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", true);
                resp.put("message", "Cập nhật số lượng thành công!");
                resp.put("totalCount", cart.getTongSoLuong());
                resp.put("tamTinh", cart.getTamTinh());
                resp.put("phiVanChuyen", cart.getPhiVanChuyen());
                resp.put("tongTien", cart.getTongTien());
                resp.put("tienDoFreeship", cart.getTienDoFreeship());
                resp.put("tienConThieuFreeship", cart.getTienConThieuFreeship());
                return ResponseEntity.ok(resp);
            }

            ra.addFlashAttribute("thongBaoThanhCong", "Đã cập nhật giỏ hàng!");
            return "redirect:/gio-hang";

        } catch (IllegalArgumentException ex) {
            if (isAjax) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", ex.getMessage());
                return ResponseEntity.badRequest().body(resp);
            }
            ra.addFlashAttribute("thongBaoLoi", ex.getMessage());
            return "redirect:/gio-hang";
        }
    }

    // 4. Xóa một sản phẩm khỏi giỏ
    @GetMapping("/xoa/{id}")
    public String xoaSanPham(@PathVariable("id") Integer sanPhamChiTietId,
                             HttpSession session,
                             RedirectAttributes ra) {
        gioHangService.xoaSanPham(session, sanPhamChiTietId);
        ra.addFlashAttribute("thongBaoThanhCong", "Đã xóa sản phẩm khỏi giỏ hàng!");
        return "redirect:/gio-hang";
    }

    @PostMapping("/xoa/{id}")
    public Object xoaSanPhamPost(@PathVariable("id") Integer sanPhamChiTietId,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 RedirectAttributes ra) {
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        CartDto cart = gioHangService.xoaSanPham(session, sanPhamChiTietId);

        if (isAjax) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Đã xóa sản phẩm khỏi giỏ hàng!");
            resp.put("totalCount", cart.getTongSoLuong());
            resp.put("tamTinh", cart.getTamTinh());
            resp.put("phiVanChuyen", cart.getPhiVanChuyen());
            resp.put("tongTien", cart.getTongTien());
            return ResponseEntity.ok(resp);
        }

        ra.addFlashAttribute("thongBaoThanhCong", "Đã xóa sản phẩm khỏi giỏ hàng!");
        return "redirect:/gio-hang";
    }

    // 5. Xóa toàn bộ giỏ hàng
    @PostMapping("/xoa-tat-ca")
    public String xoaTatCa(HttpSession session, RedirectAttributes ra) {
        gioHangService.xoaTatCa(session);
        ra.addFlashAttribute("thongBaoThanhCong", "Đã làm trống giỏ hàng!");
        return "redirect:/gio-hang";
    }

    // 6. API lấy số lượng giỏ hàng realtime cho header badge
    @GetMapping("/so-luong")
    @ResponseBody
    public Map<String, Object> laySoLuongGioHang(HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("totalCount", gioHangService.getTongSoLuong(session));
        return resp;
    }
}
