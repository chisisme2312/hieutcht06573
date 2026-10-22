package com.luonvuituoi.shop.util;

import com.luonvuituoi.shop.entity.SanPham;
import com.luonvuituoi.shop.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseFontFixer implements CommandLineRunner {

    private final SanPhamRepository sanPhamRepository;

    @Override
    @Transactional
    public void run(String... args) {
        fixProductNames();
    }

    @Transactional
    public void fixProductNames() {
        Map<String, String> properNames = new HashMap<>();
        properNames.put("AT01", "Áo Thun Cotton Basic Luôn Vui Tươi");
        properNames.put("AP01", "Áo Polo Pique Cổ Bẻ Form Regular");
        properNames.put("SM01", "Áo Sơ Mi Dài Tay Chống Nhăn");

        properNames.put("AT02", "Áo Thun Oversize Unisex In Hình Retro");
        properNames.put("AT03", "Áo Thun Cổ Tròn Cotton Compact Kháng Khuẩn");
        properNames.put("AT04", "Áo Thun Tay Lỡ Streetwear Form Rộng");
        properNames.put("AT05", "Áo Thun Raglan Phối Màu Thể Thao Nam");

        properNames.put("AP02", "Áo Polo Dệt Tổ Ong Thấm Hút Mồ Hôi");
        properNames.put("AP03", "Áo Polo Cổ Bo Phối Viền Tương Phản");
        properNames.put("AP04", "Áo Polo Thể Thao Co Giãn 4 Chiều Active");
        properNames.put("AP05", "Áo Polo Cổ Khóa Kéo Zipper Hiện Đại");

        properNames.put("SM02", "Áo Sơ Mi Oxford Dài Tay Form Slimfit");
        properNames.put("SM03", "Áo Sơ Mi Cổ Trụ Vải Modal Mềm Mát");
        properNames.put("SM04", "Áo Sơ Mi Tay Ngắn Họa Tiết Kẻ Caro");
        properNames.put("SM05", "Áo Sơ Mi Linen Nam Phong Cách Mộc");

        properNames.put("HD01", "Áo Hoodie Nỉ Bông Unisex Phom Rộng");
        properNames.put("HD02", "Áo Sweater Dài Tay Thêu Chữ Nổi Basic");
        properNames.put("HD03", "Áo Hoodie Zip Có Khóa Kéo Cao Cấp");

        properNames.put("JK01", "Áo Khoác Bomber Lót Dù 2 Lớp Cản Gió");
        properNames.put("JK02", "Áo Khoác Gió Thể Thao Chống Nước");
        properNames.put("JK03", "Áo Khoác Denim Bò Cổ Bẻ Bụi Bặm");

        Map<String, String> properDescriptions = new HashMap<>();
        properDescriptions.put("AT01", "Chất liệu cotton 100% co giãn 4 chiều mềm mịn, thấm hút mồ hôi tối đa.");
        properDescriptions.put("AP01", "Vải Pique mắt chim cao cấp dệt tổ ong, đứng dáng, chuẩn phong cách quý ông.");
        properDescriptions.put("SM01", "Chất liệu lụa pha modal chống nhăn tuyệt đối, thoáng mát, form dáng thanh lịch.");

        properDescriptions.put("AT02", "Chất liệu cotton 100% thoáng mát, form rộng unisex cá tính.");
        properDescriptions.put("AT03", "Sợi Compact chống xù lông, công nghệ kháng khuẩn khử mùi vượt trội.");
        properDescriptions.put("AT04", "Phong cách đường phố trẻ trung năng động, dễ dàng phối đồ dạo phố.");
        properDescriptions.put("AT05", "Thiết kế tay raglan phong cách bóng chày cá tính, co giãn nhẹ.");

        properDescriptions.put("AP02", "Mặt vải dệt tổ ong thông thoáng, thấm hút cực nhanh, chuẩn form công sở.");
        properDescriptions.put("AP03", "Bo cổ và tay dệt sọc thể thao tinh tế, lịch sự trong mọi hoàn cảnh.");
        properDescriptions.put("AP04", "Vải co giãn thể thao cao cấp, chuyên dụng vận động ngoài trời và golf.");
        properDescriptions.put("AP05", "Thiết kế khóa kéo kim loại thay thế hàng nút truyền thống sang trọng.");

        properDescriptions.put("SM02", "Vải Oxford dệt dày dặn, đứng dáng, phù hợp đi làm và gặp đối tác.");
        properDescriptions.put("SM03", "Chất liệu sợi gỗ sồi modal siêu mềm mịn, chống nhăn tự nhiên.");
        properDescriptions.put("SM04", "Họa tiết caro thanh lịch, form regular fit thoải mái cả ngày hè.");
        properDescriptions.put("SM05", "Chất đũi linen tự nhiên thoáng mát, mang lại cảm giác thư thái.");

        properDescriptions.put("HD01", "Nỉ bông dày dặn ấm áp, có mũ trùm đôi và túi kangaroo tiện dụng.");
        properDescriptions.put("HD02", "Bo gấu dệt kim co giãn, thêu logo sắc nét ngực áo trẻ trung.");
        properDescriptions.put("HD03", "Khóa kéo 2 chiều trơn tru, dễ dàng biến tấu phong cách layer ấm áp.");

        properDescriptions.put("JK01", "Lớp dù chống thấm nước nhẹ, lót trong giữ nhiệt ấm áp mùa thu đông.");
        properDescriptions.put("JK02", "Gấp gọn tiện lợi, chuyên dụng cho đi phượt và hoạt động thể thao.");
        properDescriptions.put("JK03", "Chất jean wash bền màu, form suông khỏe khoắn đậm chất nam tính.");

        for (SanPham sp : sanPhamRepository.findAll()) {
            String ma = sp.getMaSanPham();
            if (ma != null && properNames.containsKey(ma)) {
                sp.setTenSanPham(properNames.get(ma));
                if (properDescriptions.containsKey(ma)) {
                    sp.setMoTa(properDescriptions.get(ma));
                }
                sanPhamRepository.save(sp);
            }
        }
        log.info("==> [DatabaseFontFixer] Đã sửa lỗi font tiếng Việt cho toàn bộ danh mục sản phẩm!");
    }
}
