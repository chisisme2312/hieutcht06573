package com.luonvuituoi.shop;

import com.luonvuituoi.shop.entity.SanPhamChiTiet;
import com.luonvuituoi.shop.repository.SanPhamChiTietRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TonKhoConcurrencyTest {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Test
    @DisplayName("Kiểm thử concurrency: Kho có 50 cái áo, 2 khách cùng mua 50 cái cùng lúc -> Đúng 1 khách thành công, 1 khách bị chặn")
    void testHaiKhachCungMua50CaiAo() throws ExecutionException, InterruptedException {
        // 1. Tìm hoặc lấy một sản phẩm chi tiết mẫu
        var list = sanPhamChiTietRepository.findAll();
        if (list.isEmpty()) {
            return; // Nếu DB rỗng bỏ qua
        }
        SanPhamChiTiet spct = list.get(0);
        Integer id = spct.getId();
        int initialTonKho = spct.getSoLuongTon() != null ? spct.getSoLuongTon() : 0;

        // Giả lập kho đang có đúng 50 cái áo
        spct.setSoLuongTon(50);
        sanPhamChiTietRepository.saveAndFlush(spct);

        // 2. Chạy 2 threads cùng lúc cố gắng mua 50 cái áo
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        CompletableFuture<Void> khachA = CompletableFuture.runAsync(() -> {
            try {
                int updated = sanPhamChiTietRepository.truTonKhoAtomic(id, 50);
                if (updated > 0) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        CompletableFuture<Void> khachB = CompletableFuture.runAsync(() -> {
            try {
                int updated = sanPhamChiTietRepository.truTonKhoAtomic(id, 50);
                if (updated > 0) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        CompletableFuture.allOf(khachA, khachB).join();

        // 3. Khẳng định: Chính xác 1 khách mua được 50 cái, khách còn lại bị chặn
        assertEquals(1, successCount.get(), "Chỉ đúng 1 khách hàng mua được 50 cái áo");
        assertEquals(1, failCount.get(), "Khách thứ hai phải nhận thông báo hết hàng / thất bại");

        // Kiểm tra tồn kho sau khi trừ phải là 0, tuyệt đối không bị âm
        var spctSauMua = sanPhamChiTietRepository.findById(id).orElseThrow();
        assertEquals(0, spctSauMua.getSoLuongTon(), "Tồn kho sau khi 1 khách mua 50 cái phải là 0");

        // Hoàn lại kho như ban đầu để bảo toàn dữ liệu
        spct.setSoLuongTon(initialTonKho);
        sanPhamChiTietRepository.saveAndFlush(spct);
    }
}
