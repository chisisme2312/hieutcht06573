# 📋 HƯỚNG DẪN PHÂN CHIA NHIỆM VỤ NHÓM LÀM ĐỒ ÁN TỐT NGHIỆP
> **Dự án**: Hệ thống quản lý bán hàng thời trang Luôn Vui Tươi (Spring Boot 3 + Thymeleaf + SQL Server)

Khung dự án đã được khởi tạo sẵn sàng toàn bộ:
- Cấu hình Maven `pom.xml` đầy đủ thư viện.
- Cấu hình `application.properties` kết nối CSDL SQL Server `luon_vui_tuoi_shop`.
- Đầy đủ 18 class **Entity** tương ứng 18 bảng.
- Đầy đủ 18 interface **Repository** Spring Data JPA.

Các thành viên chỉ cần mở dự án bằng **IntelliJ IDEA** và nhận module theo phân công dưới đây để mỗi người đều có code và có điểm cao:

---

### 👨‍💻 THÀNH VIÊN 1: QUẢN LÝ THUỘC TÍNH & SẢN PHẨM (Admin)
* **Nhiệm vụ:**
  1. Làm các trang CRUD Thuộc tính áo trong Admin: Màu sắc, Kích thước, Loại áo, Chất liệu, Cổ áo, Nhà cung cấp.
  2. Làm trang Quản lý Mẫu áo (`/admin/san-pham`): Thêm áo mới, sửa áo, upload ảnh.
  3. Làm Quản lý biến thể áo (`san_pham_chi_tiet`): Nhập số lượng tồn kho từng size và màu.
* **Các file code chính:** `SanPhamAdminController`, `ThuocTinhAdminController`, `SanPhamService`.

---

### 👩‍💻 THÀNH VIÊN 2: BÁN HÀNG ONLINE & GIỎ HÀNG (Phía Khách hàng - Client)
* **Nhiệm vụ:**
  1. Ghép giao diện trang chủ bán hàng (`/`): Hiển thị danh sách áo đẹp mắt, banner khuyến mãi.
  2. Làm chức năng Tìm kiếm (Search) và Bộ lọc (Filter theo giá, size, màu sắc).
  3. Làm trang Chi tiết sản phẩm: Khách bấm chọn màu + size $\rightarrow$ hiện số lượng còn trong kho.
  4. Làm chức năng Thêm vào giỏ hàng, Cập nhật số lượng giỏ hàng, Xóa khỏi giỏ.
* **Các file code chính:** `HomeController`, `ProductClientController`, `CartClientController`.

---

### 👨‍💻 THÀNH VIÊN 3: ĐẶT HÀNG, VOUCHER & BÁN TẠI QUẦY (POS)
* **Nhiệm vụ:**
  1. Làm trang Thanh toán (Checkout) phía khách: Điền địa chỉ nhận hàng, nhập mã Voucher giảm giá $\rightarrow$ Tạo hóa đơn online.
  2. Làm trang Bán hàng tại quầy (POS) cho thu ngân: Chọn áo $\rightarrow$ in hóa đơn bán trực tiếp tại shop.
  3. Quản lý Voucher / Giảm giá: Tạo mã voucher mới, đặt giới hạn lượt dùng, hạn sử dụng.
* **Các file code chính:** `CheckoutController`, `PosAdminController`, `GiamGiaAdminController`, `HoaDonService`.

---

### 👩‍💻 THÀNH VIÊN 4: QUẢN LÝ HÓA ĐƠN, NHÂN VIÊN & THỐNG KÊ (Admin)
* **Nhiệm vụ:**
  1. Làm trang Quản lý Hóa đơn: Duyệt đơn online (*Chờ xác nhận $\rightarrow$ Đang giao $ightarrow$ Hoàn thành $ightarrow$ Hủy đơn*).
  2. Làm trang Quản lý Nhân viên & Khách hàng.
  3. Làm Dashboard Thống kê: Doanh thu theo ngày/tháng, số đơn hàng thành công, top áo bán chạy nhất.
* **Các file code chính:** `HoaDonAdminController`, `NhanVienAdminController`, `AdminDashboardController`.

---

### 🚀 CÁCH CHẠY DỰ ÁN TRÊN INTELLIJ IDEA:
1. Mở **IntelliJ IDEA** $ightarrow$ chọn **Open** $ightarrow$ chọn thư mục `luon-vui-tuoi-shop`.
2. Đợi IntelliJ load thư viện Maven xong (khoảng 1-2 phút lần đầu).
3. Tìm file `com.luonvuituoi.shop.LuonVuiTuoiShopApplication.java` $ightarrow$ click chuột phải chọn **Run**.
4. Mở trình duyệt truy cập:
   - Trang bán hàng: `http://localhost:8080/`
   - Trang quản trị: `http://localhost:8080/admin`
