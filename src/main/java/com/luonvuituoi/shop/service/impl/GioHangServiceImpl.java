package com.luonvuituoi.shop.service.impl;

import com.luonvuituoi.shop.dto.CartDto;
import com.luonvuituoi.shop.dto.CartItemDto;
import com.luonvuituoi.shop.entity.Anh;
import com.luonvuituoi.shop.entity.GioHangChiTiet;
import com.luonvuituoi.shop.entity.KhachHang;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.repository.GioHangChiTietRepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.repository.SanPhamChiTietRepository;
import com.luonvuituoi.shop.service.GioHangService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GioHangServiceImpl implements GioHangService {

    public static final String SESSION_CART_KEY = "SESSION_CART";

    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final KhachHangRepository khachHangRepository;

    @Override
    public CartDto getGioHang(HttpSession session) {
        CartDto cart = (CartDto) session.getAttribute(SESSION_CART_KEY);
        if (cart == null) {
            cart = CartDto.builder().items(new ArrayList<>()).build();
            session.setAttribute(SESSION_CART_KEY, cart);
        }
        return cart;
    }

    @Override
    @Transactional
    public CartDto themSanPham(HttpSession session, Integer sanPhamChiTietId, Integer soLuong) {
        if (sanPhamChiTietId == null || soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("Thông tin sản phẩm hoặc số lượng không hợp lệ!");
        }

        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(sanPhamChiTietId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biến thể sản phẩm yêu cầu!"));

        if (Boolean.FALSE.equals(spct.getTrangThai())) {
            throw new IllegalArgumentException("Sản phẩm này hiện đang ngừng kinh doanh!");
        }

        int tonKho = spct.getSoLuongTon() != null ? spct.getSoLuongTon() : 0;
        if (tonKho <= 0) {
            throw new IllegalArgumentException("Sản phẩm với biến thể này hiện đã hết hàng!");
        }

        CartDto cart = getGioHang(session);
        Optional<CartItemDto> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getSanPhamChiTietId().equals(sanPhamChiTietId))
                .findFirst();

        int currentQtyInCart = existingItemOpt.map(CartItemDto::getSoLuong).orElse(0);
        int targetQty = currentQtyInCart + soLuong;

        if (targetQty > tonKho) {
            throw new IllegalArgumentException("Số lượng trong giỏ (" + targetQty + ") vượt quá tồn kho hiện tại (" + tonKho + ")!");
        }

        if (existingItemOpt.isPresent()) {
            CartItemDto item = existingItemOpt.get();
            item.setSoLuong(targetQty);
            item.setSoLuongTon(tonKho);
            item.setGiaBan(spct.getGiaBan());
            item.setGiaBanBanDau(spct.getGiaBanBanDau());
        } else {
            String anhUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600";
            if (spct.getSanPham() != null && spct.getSanPham().getDanhSachAnh() != null && !spct.getSanPham().getDanhSachAnh().isEmpty()) {
                anhUrl = spct.getSanPham().getDanhSachAnh().iterator().next().getAnhUrl();
            }

            CartItemDto newItem = CartItemDto.builder()
                    .sanPhamChiTietId(spct.getId())
                    .sanPhamId(spct.getSanPham() != null ? spct.getSanPham().getId() : null)
                    .maSanPham(spct.getSanPham() != null ? spct.getSanPham().getMaSanPham() : "")
                    .tenSanPham(spct.getSanPham() != null ? spct.getSanPham().getTenSanPham() : "")
                    .anhUrl(anhUrl)
                    .mauSacId(spct.getMauSac() != null ? spct.getMauSac().getId() : null)
                    .tenMauSac(spct.getMauSac() != null ? spct.getMauSac().getTenMauSac() : "")
                    .kichThuocId(spct.getKichThuoc() != null ? spct.getKichThuoc().getId() : null)
                    .tenKichThuoc(spct.getKichThuoc() != null ? spct.getKichThuoc().getTenKichThuoc() : "")
                    .giaBan(spct.getGiaBan())
                    .giaBanBanDau(spct.getGiaBanBanDau())
                    .soLuong(soLuong)
                    .soLuongTon(tonKho)
                    .build();

            cart.getItems().add(newItem);
        }

        session.setAttribute(SESSION_CART_KEY, cart);

        // Lưu hoặc đồng bộ xuống Database bảng gio_hang_chi_tiet nếu có khách hàng
        luuVaoDatabaseNeuCoKhach(session, spct, targetQty);

        return cart;
    }

    @Override
    @Transactional
    public CartDto capNhatSoLuong(HttpSession session, Integer sanPhamChiTietId, Integer soLuong) {
        if (sanPhamChiTietId == null) {
            throw new IllegalArgumentException("Biến thể sản phẩm không hợp lệ!");
        }

        CartDto cart = getGioHang(session);

        if (soLuong == null || soLuong <= 0) {
            return xoaSanPham(session, sanPhamChiTietId);
        }

        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(sanPhamChiTietId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biến thể sản phẩm!"));

        int tonKho = spct.getSoLuongTon() != null ? spct.getSoLuongTon() : 0;
        if (soLuong > tonKho) {
            throw new IllegalArgumentException("Số lượng yêu cầu (" + soLuong + ") vượt quá tồn kho (" + tonKho + ")!");
        }

        for (CartItemDto item : cart.getItems()) {
            if (item.getSanPhamChiTietId().equals(sanPhamChiTietId)) {
                item.setSoLuong(soLuong);
                item.setSoLuongTon(tonKho);
                item.setGiaBan(spct.getGiaBan());
                break;
            }
        }

        session.setAttribute(SESSION_CART_KEY, cart);
        luuVaoDatabaseNeuCoKhach(session, spct, soLuong);

        return cart;
    }

    @Override
    @Transactional
    public CartDto xoaSanPham(HttpSession session, Integer sanPhamChiTietId) {
        CartDto cart = getGioHang(session);
        cart.getItems().removeIf(item -> item.getSanPhamChiTietId().equals(sanPhamChiTietId));
        session.setAttribute(SESSION_CART_KEY, cart);

        // Xóa trong DB nếu có khách
        Object userObj = session.getAttribute("currentKhachHang");
        if (userObj instanceof KhachHang kh) {
            gioHangChiTietRepository.deleteByKhachHangIdAndSanPhamChiTietId(kh.getId(), sanPhamChiTietId);
        }
        return cart;
    }

    @Override
    @Transactional
    public void xoaTatCa(HttpSession session) {
        CartDto cart = getGioHang(session);
        cart.getItems().clear();
        session.setAttribute(SESSION_CART_KEY, cart);

        Object userObj = session.getAttribute("currentKhachHang");
        if (userObj instanceof KhachHang kh) {
            gioHangChiTietRepository.deleteByKhachHangId(kh.getId());
        }
    }

    @Override
    public int getTongSoLuong(HttpSession session) {
        CartDto cart = getGioHang(session);
        return cart.getTongSoLuong();
    }

    @Override
    @Transactional
    public void dongBoVaoDatabase(HttpSession session, Integer khachHangId) {
        if (khachHangId == null) return;
        KhachHang kh = khachHangRepository.findById(khachHangId).orElse(null);
        if (kh == null) return;

        CartDto cart = getGioHang(session);
        for (CartItemDto item : cart.getItems()) {
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getSanPhamChiTietId()).orElse(null);
            if (spct == null) continue;

            Optional<GioHangChiTiet> ghctOpt = gioHangChiTietRepository.findByKhachHangIdAndSanPhamChiTietId(kh.getId(), spct.getId());
            if (ghctOpt.isPresent()) {
                GioHangChiTiet ghct = ghctOpt.get();
                ghct.setSoLuong(item.getSoLuong());
                gioHangChiTietRepository.save(ghct);
            } else {
                GioHangChiTiet ghct = GioHangChiTiet.builder()
                        .khachHang(kh)
                        .sanPhamChiTiet(spct)
                        .soLuong(item.getSoLuong())
                        .ngayTao(LocalDateTime.now())
                        .build();
                gioHangChiTietRepository.save(ghct);
            }
        }
    }

    private void luuVaoDatabaseNeuCoKhach(HttpSession session, SanPhamChiTiet spct, int soLuong) {
        Object userObj = session.getAttribute("currentKhachHang");
        if (userObj instanceof KhachHang kh) {
            Optional<GioHangChiTiet> ghctOpt = gioHangChiTietRepository.findByKhachHangIdAndSanPhamChiTietId(kh.getId(), spct.getId());
            if (ghctOpt.isPresent()) {
                GioHangChiTiet ghct = ghctOpt.get();
                ghct.setSoLuong(soLuong);
                gioHangChiTietRepository.save(ghct);
            } else {
                GioHangChiTiet ghct = GioHangChiTiet.builder()
                        .khachHang(kh)
                        .sanPhamChiTiet(spct)
                        .soLuong(soLuong)
                        .ngayTao(LocalDateTime.now())
                        .build();
                gioHangChiTietRepository.save(ghct);
            }
        }
    }
}
