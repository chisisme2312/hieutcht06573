package com.luonvuituoi.shop.controller.admin;

import com.luonvuituoi.shop.entity.Anh;
import com.luonvuituoi.shop.entity.SanPham;
import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.service.SanPhamChiTietService;
import com.luonvuituoi.shop.service.SanPhamService;
import com.luonvuituoi.shop.service.ThuocTinhService;
import com.luonvuituoi.shop.util.DatabaseFontFixer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/san-pham")
@RequiredArgsConstructor
public class SanPhamAdminController {

    private final SanPhamService sanPhamService;
    private final SanPhamChiTietService sanPhamChiTietService;
    private final ThuocTinhService thuocTinhService;
    private final DatabaseFontFixer databaseFontFixer;

    // 1.0 Endpoint Sửa Font tiếng Việt Unicode cho toàn bộ sản phẩm
    @GetMapping("/sua-font")
    public String suaFont(RedirectAttributes ra) {
        databaseFontFixer.fixProductNames();
        ra.addFlashAttribute("thongBao", "Đã cập nhật lại toàn bộ font chữ tiếng Việt chuẩn Unicode cho sản phẩm!");
        return "redirect:/admin/san-pham";
    }

    // 1. Danh sách sản phẩm (Phân trang & Lọc đa tiêu chí)
    @GetMapping
    public String danhSachSanPham(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "loaiSanPhamId", required = false) Integer loaiSanPhamId,
                                  @RequestParam(value = "trangThai", required = false) Boolean trangThai,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "8") int size,
                                  Model model) {
        Page<SanPham> pageSanPham = sanPhamService.filterSanPham(keyword, loaiSanPhamId, trangThai, page, size);

        model.addAttribute("pageSanPham", pageSanPham);
        model.addAttribute("danhSach", pageSanPham.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageSanPham.getTotalPages());
        model.addAttribute("totalItems", pageSanPham.getTotalElements());
        model.addAttribute("pageSize", size);

        // Giữ lại bộ lọc
        model.addAttribute("keyword", keyword);
        model.addAttribute("loaiSanPhamId", loaiSanPhamId);
        model.addAttribute("trangThai", trangThai);

        // Đổ danh sách loại áo cho dropdown lọc
        model.addAttribute("listLoaiSanPham", thuocTinhService.getAllLoaiSanPham());

        return "admin/san-pham/index";
    }

    // 1.1 Bật / Tắt trạng thái kinh doanh nhanh
    @GetMapping("/doi-trang-thai/{id}")
    public String doiTrangThai(@PathVariable("id") Integer id, RedirectAttributes ra) {
        var spOpt = sanPhamService.getById(id);
        if (spOpt.isPresent()) {
            SanPham sp = spOpt.get();
            boolean trangThaiMoi = !Boolean.TRUE.equals(sp.getTrangThai());
            sp.setTrangThai(trangThaiMoi);
            sanPhamService.save(sp);
            ra.addFlashAttribute("thongBao", "Đã chuyển trạng thái mẫu áo [" + sp.getTenSanPham() + "] thành: " 
                    + (trangThaiMoi ? "🟢 Đang kinh doanh (Hiển thị ở trang bán hàng)" : "⚪ Ngừng kinh doanh (Tạm ẩn khỏi trang khách)"));
        }
        return "redirect:/admin/san-pham";
    }

    // 1.2 Xuất toàn bộ danh sách sản phẩm ra Excel (.xlsx)
    @GetMapping("/xuat-excel")
    public void xuatExcel(HttpServletResponse response) throws IOException {
        List<SanPham> danhSach = sanPhamService.getAll();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerValue = "attachment; filename=Danh_Sach_San_Pham_LuonVuiTuoi_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader("Content-Disposition", headerValue);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh Sách Mẫu Áo");

            // Style Tiêu Đề Chính
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 15);
            titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style Header Bảng
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 10);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Style Dòng Dữ Liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(dataStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Dòng Tiêu Đề
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO DANH SÁCH MẪU ÁO - LUÔN VUI TƯƠI SHOP");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            // Dòng Tiêu Đề Cột
            String[] headers = {"STT", "Mã Sản Phẩm", "Tên Mẫu Áo", "Loại Áo", "Chất Liệu", "Nhà Cung Cấp", "Số Biến Thể", "Tổng Tồn Kho", "Trạng Thái"};
            Row headerRow = sheet.createRow(2);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dữ Liệu Các Dòng
            int rowIdx = 3;
            int stt = 1;
            for (SanPham sp : danhSach) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);

                Cell c0 = row.createCell(0);
                c0.setCellValue(stt++);
                c0.setCellStyle(centerStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(sp.getMaSanPham() != null ? sp.getMaSanPham() : "");
                c1.setCellStyle(centerStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(sp.getTenSanPham() != null ? sp.getTenSanPham() : "");
                c2.setCellStyle(dataStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoaiSanPham() : "-");
                c3.setCellStyle(dataStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(sp.getChatLieu() != null ? sp.getChatLieu().getTenChatLieu() : "-");
                c4.setCellStyle(dataStyle);

                Cell c5 = row.createCell(5);
                c5.setCellValue(sp.getNhaCungCap() != null ? sp.getNhaCungCap().getTenNhaCungCap() : "-");
                c5.setCellStyle(dataStyle);

                int soBienThe = (sp.getDanhSachChiTiet() != null) ? sp.getDanhSachChiTiet().size() : 0;
                int tongTon = 0;
                if (sp.getDanhSachChiTiet() != null) {
                    for (SanPhamChiTiet ct : sp.getDanhSachChiTiet()) {
                        if (ct.getSoLuongTon() != null) {
                            tongTon += ct.getSoLuongTon();
                        }
                    }
                }

                Cell c6 = row.createCell(6);
                c6.setCellValue(soBienThe);
                c6.setCellStyle(centerStyle);

                Cell c7 = row.createCell(7);
                c7.setCellValue(tongTon);
                c7.setCellStyle(centerStyle);

                Cell c8 = row.createCell(8);
                c8.setCellValue(Boolean.TRUE.equals(sp.getTrangThai()) ? "Đang kinh doanh" : "Ngừng bán");
                c8.setCellStyle(centerStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i) + 1200, 3200));
            }

            workbook.write(response.getOutputStream());
        }
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
            String msg = e.getMessage();
            if (msg != null && (msg.contains("duplicate") || msg.contains("UNIQUE") || msg.contains("ma_san_pham"))) {
                ra.addFlashAttribute("loi", "Mã sản phẩm [" + sanPham.getMaSanPham() + "] đã tồn tại, vui lòng nhập mã khác!");
            } else {
                ra.addFlashAttribute("loi", "Có lỗi xảy ra: " + e.getMessage());
            }
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
                
                // Nạp thực thể MauSac và KichThuoc đầy đủ
                if (chiTiet.getMauSac() != null && chiTiet.getMauSac().getId() != null) {
                    thuocTinhService.getMauSacById(chiTiet.getMauSac().getId()).ifPresent(chiTiet::setMauSac);
                }
                if (chiTiet.getKichThuoc() != null && chiTiet.getKichThuoc().getId() != null) {
                    thuocTinhService.getKichThuocById(chiTiet.getKichThuoc().getId()).ifPresent(chiTiet::setKichThuoc);
                }

                if (chiTiet.getTrangThai() == null) {
                    chiTiet.setTrangThai(true);
                }
                
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
            String msg = e.getMessage();
            if (msg != null && (msg.contains("duplicate") || msg.contains("UNIQUE") || msg.contains("ma_san_pham_chi_tiet"))) {
                ra.addFlashAttribute("loi", "Biến thể với Màu sắc và Size này đã tồn tại!");
            } else {
                ra.addFlashAttribute("loi", "Lỗi lưu biến thể: " + e.getMessage());
            }
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
