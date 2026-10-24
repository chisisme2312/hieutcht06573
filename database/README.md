# Hướng dẫn khởi tạo Cơ sở dữ liệu (SQL Server)

Dự án sử dụng Hệ quản trị CSDL **Microsoft SQL Server 2022** (hoặc 2019/2016).

---

## 1. File kịch bản (Script SQL)
- File script chính: `db_luon_vui_tuoi.sql` (gồm cấu trúc 18 bảng + dữ liệu mẫu khởi tạo chuẩn đồ án).

---

## 2. Các bước Import vào SQL Server (qua SSMS)

1. Mở **SQL Server Management Studio (SSMS)** và đăng nhập vào SQL Server (Localhost).
2. Vào menu **File** -> **Open** -> **File...** (hoặc nhấn tổ hợp phím `Ctrl + O`).
3. Chọn đường dẫn tới file `database/db_luon_vui_tuoi.sql` trong thư mục dự án.
4. Nhấn nút **Execute** (hoặc phím `F5`) để chạy script.
5. Sau khi chạy xong, Refresh lại mục **Databases** trên Object Explorer sẽ thấy cơ sở dữ liệu `luon_vui_tuoi_shop`.

---

## 3. Cấu hình kết nối trong dự án
File cấu hình tại `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=luon_vui_tuoi_shop;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=123456
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```
*Lưu ý: Nếu mật khẩu tài khoản `sa` của bạn khác `123456`, vui lòng cập nhật lại mật khẩu trong `application.properties` tương ứng.*
