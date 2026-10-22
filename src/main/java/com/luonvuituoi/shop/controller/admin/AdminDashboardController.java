package com.luonvuituoi.shop.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luonvuituoi.shop.entity.HoaDon;
import com.luonvuituoi.shop.entity.HoaDonChiTiet;
import com.luonvuituoi.shop.entity.SanPham;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.repository.HoaDonChiTietRepository;
import com.luonvuituoi.shop.repository.HoaDonRepository;
import com.luonvuituoi.shop.repository.KhachHangRepository;
import com.luonvuituoi.shop.repository.SanPhamChiTietRepository;
import com.luonvuituoi.shop.repository.SanPhamRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final ObjectMapper objectMapper;

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("###,###,###");

    @Data
    @Builder
    public static class TopProductDto {
        private Integer id;
        private String maSanPham;
        private String tenSanPham;
        private String loaiSanPham;
        private String anhDaiDien;
        private int daBan;
        private BigDecimal tongDoanhThu;
        private BigDecimal giaBan;
        private int tonKho;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<HoaDon> allHoaDon = hoaDonRepository.findAll();
        List<SanPhamChiTiet> allChiTiet = sanPhamChiTietRepository.findAll();
        List<SanPham> allSanPham = sanPhamRepository.findAll();
        long totalCustomers = khachHangRepository.count();

        // 1. Thống kê số lượng đơn và trạng thái
        long soDonHoanThanh = allHoaDon.stream()
                .filter(h -> h.getTrangThai() != null && h.getTrangThai().contains("hoàn thành"))
                .count();

        long soDonDangGiao = allHoaDon.stream()
                .filter(h -> h.getTrangThai() != null && h.getTrangThai().contains("Đang giao"))
                .count();

        long soDonChoXuLy = allHoaDon.stream()
                .filter(h -> h.getTrangThai() != null && (h.getTrangThai().contains("Chờ") || h.getTrangThai().contains("chờ")))
                .count();

        long soDonHuy = allHoaDon.stream()
                .filter(h -> h.getTrangThai() != null && h.getTrangThai().contains("hủy"))
                .count();

        long tongSoDon = allHoaDon.size();

        // 2. Thống kê Doanh thu (Tính trên các đơn Đã hoàn thành hoặc tất cả đơn trừ Đã hủy)
        BigDecimal tongDoanhThu = allHoaDon.stream()
                .filter(h -> h.getTrangThai() != null && !h.getTrangThai().contains("hủy"))
                .map(h -> h.getTongTien() != null ? h.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate now = LocalDate.now();
        BigDecimal doanhThuThangNay = allHoaDon.stream()
                .filter(h -> h.getNgayTao() != null && h.getNgayTao().getMonth() == now.getMonth() && h.getNgayTao().getYear() == now.getYear())
                .filter(h -> h.getTrangThai() != null && !h.getTrangThai().contains("hủy"))
                .map(h -> h.getTongTien() != null ? h.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Tồn kho và Cảnh báo
        int tongTonKho = allChiTiet.stream()
                .mapToInt(ct -> ct.getSoLuongTon() != null ? ct.getSoLuongTon() : 0)
                .sum();

        List<SanPhamChiTiet> danhSachSapHet = allChiTiet.stream()
                .filter(ct -> ct.getSoLuongTon() != null && ct.getSoLuongTon() <= 10)
                .sorted(Comparator.comparing(SanPhamChiTiet::getSoLuongTon))
                .limit(6)
                .collect(Collectors.toList());

        long soBienTheSapHet = allChiTiet.stream()
                .filter(ct -> ct.getSoLuongTon() != null && ct.getSoLuongTon() <= 10)
                .count();

        // 4. Danh sách đơn hàng mới nhất
        List<HoaDon> danhSachDonMoi = allHoaDon.stream()
                .sorted((a, b) -> {
                    if (a.getNgayTao() == null) return 1;
                    if (b.getNgayTao() == null) return -1;
                    return b.getNgayTao().compareTo(a.getNgayTao());
                })
                .limit(7)
                .collect(Collectors.toList());

        // 5. Top sản phẩm bán chạy nhất
        List<HoaDonChiTiet> allHdct = hoaDonChiTietRepository.findAll();
        Map<Integer, Integer> productSoldMap = new HashMap<>();
        Map<Integer, BigDecimal> productRevenueMap = new HashMap<>();

        for (HoaDonChiTiet ct : allHdct) {
            if (ct.getSanPhamChiTiet() != null && ct.getSanPhamChiTiet().getSanPham() != null) {
                Integer spId = ct.getSanPhamChiTiet().getSanPham().getId();
                int qty = ct.getSoLuong() != null ? ct.getSoLuong() : 0;
                BigDecimal price = ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO;
                BigDecimal subTotal = price.multiply(BigDecimal.valueOf(qty));

                productSoldMap.put(spId, productSoldMap.getOrDefault(spId, 0) + qty);
                productRevenueMap.put(spId, productRevenueMap.getOrDefault(spId, BigDecimal.ZERO).add(subTotal));
            }
        }

        List<TopProductDto> topSanPhamList = allSanPham.stream()
                .map(sp -> {
                    int sold = productSoldMap.getOrDefault(sp.getId(), 0);
                    BigDecimal rev = productRevenueMap.getOrDefault(sp.getId(), BigDecimal.ZERO);
                    String img = (sp.getDanhSachAnh() != null && !sp.getDanhSachAnh().isEmpty())
                            ? sp.getDanhSachAnh().iterator().next().getAnhUrl()
                            : "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=100&auto=format&fit=crop&q=60";
                    
                    int stock = sp.getDanhSachChiTiet() != null 
                            ? sp.getDanhSachChiTiet().stream().mapToInt(c -> c.getSoLuongTon() != null ? c.getSoLuongTon() : 0).sum()
                            : 0;

                    BigDecimal minPrice = (sp.getDanhSachChiTiet() != null && !sp.getDanhSachChiTiet().isEmpty())
                            ? sp.getDanhSachChiTiet().iterator().next().getGiaBan()
                            : BigDecimal.valueOf(199000);

                    return TopProductDto.builder()
                            .id(sp.getId())
                            .maSanPham(sp.getMaSanPham())
                            .tenSanPham(sp.getTenSanPham())
                            .loaiSanPham(sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoaiSanPham() : "Áo thun")
                            .anhDaiDien(img)
                            .daBan(sold)
                            .tongDoanhThu(rev)
                            .giaBan(minPrice)
                            .tonKho(stock)
                            .build();
                })
                .sorted(Comparator.comparingInt(TopProductDto::getDaBan).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // 6. Dữ liệu Biểu đồ (7 ngày gần nhất)
        List<String> last7DaysLabels = new ArrayList<>();
        List<Long> last7DaysRevenue = new ArrayList<>();
        List<Integer> last7DaysOrders = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = now.minusDays(i);
            String label = date.format(dayFormatter);
            last7DaysLabels.add(label);

            List<HoaDon> ordersOnDay = allHoaDon.stream()
                    .filter(h -> h.getNgayTao() != null && h.getNgayTao().toLocalDate().isEqual(date))
                    .collect(Collectors.toList());

            long revenue = ordersOnDay.stream()
                    .filter(h -> h.getTrangThai() != null && !h.getTrangThai().contains("hủy"))
                    .mapToLong(h -> h.getTongTien() != null ? h.getTongTien().longValue() : 0L)
                    .sum();

            last7DaysRevenue.add(revenue);
            last7DaysOrders.add(ordersOnDay.size());
        }

        // 7. Dữ liệu Biểu đồ (12 Tháng của năm 2026)
        List<String> monthLabels = List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12");
        List<Long> monthRevenue = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            final int month = m;
            long rev = allHoaDon.stream()
                    .filter(h -> h.getNgayTao() != null && h.getNgayTao().getYear() == now.getYear() && h.getNgayTao().getMonthValue() == month)
                    .filter(h -> h.getTrangThai() != null && !h.getTrangThai().contains("hủy"))
                    .mapToLong(h -> h.getTongTien() != null ? h.getTongTien().longValue() : 0L)
                    .sum();
            monthRevenue.add(rev);
        }

        // 8. Tỷ lệ trạng thái đơn hàng
        List<Long> statusCounts = List.of(soDonHoanThanh, soDonDangGiao, soDonChoXuLy, soDonHuy);

        // 9. Doanh số theo danh mục
        Map<String, Long> categoryCountMap = new HashMap<>();
        for (SanPham sp : allSanPham) {
            String catName = sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoaiSanPham() : "Khác";
            categoryCountMap.put(catName, categoryCountMap.getOrDefault(catName, 0L) + 1);
        }
        List<String> categoryLabels = new ArrayList<>(categoryCountMap.keySet());
        List<Long> categoryValues = categoryLabels.stream().map(categoryCountMap::get).collect(Collectors.toList());

        // Đẩy toàn bộ dữ liệu ra Model
        model.addAttribute("tongDoanhThuStr", CURRENCY_FORMAT.format(tongDoanhThu) + " đ");
        model.addAttribute("doanhThuThangNayStr", CURRENCY_FORMAT.format(doanhThuThangNay) + " đ");
        model.addAttribute("tongSoDon", tongSoDon);
        model.addAttribute("soDonHoanThanh", soDonHoanThanh);
        model.addAttribute("soDonDangGiao", soDonDangGiao);
        model.addAttribute("soDonChoXuLy", soDonChoXuLy);
        model.addAttribute("soDonHuy", soDonHuy);
        
        double tyLeThanhCong = tongSoDon > 0 ? ((double) soDonHoanThanh / tongSoDon) * 100 : 0;
        model.addAttribute("tyLeThanhCong", String.format("%.1f", tyLeThanhCong));

        model.addAttribute("tongSoMauAo", allSanPham.size());
        model.addAttribute("soMauAoDangBan", allSanPham.stream().filter(sp -> sp.getTrangThai() != null && sp.getTrangThai()).count());
        model.addAttribute("tongTonKho", tongTonKho);
        model.addAttribute("soBienTheSapHet", soBienTheSapHet);
        model.addAttribute("totalCustomers", totalCustomers);

        model.addAttribute("danhSachDonMoi", danhSachDonMoi);
        model.addAttribute("danhSachSapHet", danhSachSapHet);
        model.addAttribute("topSanPhamList", topSanPhamList);

        // Đẩy JSON cho Chart.js
        try {
            model.addAttribute("chart7DaysLabelsJson", objectMapper.writeValueAsString(last7DaysLabels));
            model.addAttribute("chart7DaysRevenueJson", objectMapper.writeValueAsString(last7DaysRevenue));
            model.addAttribute("chart7DaysOrdersJson", objectMapper.writeValueAsString(last7DaysOrders));

            model.addAttribute("chartMonthLabelsJson", objectMapper.writeValueAsString(monthLabels));
            model.addAttribute("chartMonthRevenueJson", objectMapper.writeValueAsString(monthRevenue));

            model.addAttribute("orderStatusCountsJson", objectMapper.writeValueAsString(statusCounts));

            model.addAttribute("categoryLabelsJson", objectMapper.writeValueAsString(categoryLabels));
            model.addAttribute("categoryValuesJson", objectMapper.writeValueAsString(categoryValues));
        } catch (JsonProcessingException e) {
            log.error("Lỗi parse JSON biểu đồ", e);
        }

        return "admin/dashboard";
    }
}
