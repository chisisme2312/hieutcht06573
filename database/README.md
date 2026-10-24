# Hướng dẫn Khởi tạo & Sử dụng Cơ sở Dữ liệu (SQL Server)

Dự án sử dụng Hệ quản trị CSDL **Microsoft SQL Server 2022** (hoặc 2019/2016).

---

## 1. Thông tin CSDL dự án
- **Tên Database**: `luon_vui_tuoi_shop`
- **File script**: `database/db_luon_vui_tuoi.sql`
- **Nội dung script**:
  - Tự động tạo mới database `luon_vui_tuoi_shop`.
  - Khởi tạo đầy đủ cấu trúc 18 bảng, quan hệ khóa ngoại (Foreign Keys), khóa chính (Primary Keys), chỉ mục (Indexes).
  - Đã xuất kèm **toàn bộ dữ liệu thực tế mới nhất**:
    - 25 Sản phẩm (`san_pham`)
    - 208 Biến thể chi tiết (`san_pham_chi_tiet`)
    - 17 Đơn hàng (`hoa_don`) & 28 Chi tiết đơn hàng (`hoa_don_chi_tiet`)
    - Toàn bộ danh mục: Loại sản phẩm, Màu sắc, Kích thước, Nhà cung cấp, Chất liệu, Cổ áo, Khuyến mãi, Giảm giá, Tài khoản nhân viên, Khách hàng, Giỏ hàng...

---

## 2. Các bước Import vào SQL Server (qua SSMS)

1. Mở **SQL Server Management Studio (SSMS)** và đăng nhập vào SQL Server (Localhost).
2. Vào menu **File** -> **Open** -> **File...** (hoặc nhấn `Ctrl + O`).
3. Chọn file `database/db_luon_vui_tuoi.sql` trong thư mục dự án.
4. Nhấn nút **Execute** (hoặc phím `F5`) để chạy toàn bộ script.
5. Sau khi hoàn tất, chuột phải vào mục **Databases** chọn **Refresh** để thấy database `luon_vui_tuoi_shop`.

---

## 3. Cấu hình kết nối dự án (Spring Boot)
File cấu hình tại `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=luon_vui_tuoi_shop;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=123456
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```
*(Lưu ý: Nếu mật khẩu tài khoản `sa` của bạn khác `123456`, vui lòng sửa lại tương ứng trong file `application.properties`).*
