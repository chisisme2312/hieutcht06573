-- ===============================================================
-- HỆ THỐNG QUẢN LÝ BÁN HÀNG THỜI TRANG LUÔN VUI TƯƠI
-- DATABASE: MICROSOFT SQL SERVER 2022 / SSMS
-- CHUẨN ĐỒ ÁN TỐT NGHIỆP FPT POLYTECHNIC (18 BẢNG + DỮ LIỆU MẪU)
-- ===============================================================

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'luon_vui_tuoi_shop')
BEGIN
    ALTER DATABASE luon_vui_tuoi_shop SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE luon_vui_tuoi_shop;
END
GO

CREATE DATABASE luon_vui_tuoi_shop;
GO

USE luon_vui_tuoi_shop;
GO

-- 1. Bảng loại sản phẩm
CREATE TABLE loai_san_pham (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_loai_san_pham NVARCHAR(255) NOT NULL UNIQUE,
    trang_thai BIT DEFAULT 1,
    mo_ta NVARCHAR(255),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 2. Bảng nhà cung cấp
CREATE TABLE nha_cung_cap (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_nha_cung_cap NVARCHAR(255) NOT NULL UNIQUE,
    trang_thai BIT DEFAULT 1,
    mo_ta NVARCHAR(255),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 3. Bảng chất liệu
CREATE TABLE chat_lieu (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_chat_lieu NVARCHAR(255) NOT NULL UNIQUE,
    trang_thai BIT DEFAULT 1,
    mo_ta NVARCHAR(255),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 4. Bảng cổ áo
CREATE TABLE co_ao (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_co_ao NVARCHAR(255) NOT NULL UNIQUE,
    trang_thai BIT DEFAULT 1,
    mo_ta NVARCHAR(255),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 5. Bảng màu sắc
CREATE TABLE mau_sac (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_mau_sac NVARCHAR(255) NOT NULL UNIQUE,
    trang_thai BIT DEFAULT 1,
    mo_ta NVARCHAR(255),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 6. Bảng kích thước (size)
CREATE TABLE kich_thuoc (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_kich_thuoc NVARCHAR(50) NOT NULL UNIQUE,
    trang_thai BIT DEFAULT 1,
    mo_ta NVARCHAR(255),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 7. Bảng sản phẩm (mẫu áo)
CREATE TABLE san_pham (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_san_pham VARCHAR(50) NOT NULL UNIQUE,
    ten_san_pham NVARCHAR(255) NOT NULL,
    mo_ta NVARCHAR(MAX),
    id_loai_san_pham INT NOT NULL,
    id_nha_cung_cap INT NOT NULL,
    id_chat_lieu INT NOT NULL,
    id_co_ao INT NOT NULL,
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_sp_loai FOREIGN KEY (id_loai_san_pham) REFERENCES loai_san_pham(id),
    CONSTRAINT fk_sp_ncc FOREIGN KEY (id_nha_cung_cap) REFERENCES nha_cung_cap(id),
    CONSTRAINT fk_sp_chat_lieu FOREIGN KEY (id_chat_lieu) REFERENCES chat_lieu(id),
    CONSTRAINT fk_sp_co_ao FOREIGN KEY (id_co_ao) REFERENCES co_ao(id)
);
GO

-- 8. Bảng hình ảnh
CREATE TABLE anh (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_san_pham INT NOT NULL,
    anh_url NVARCHAR(500) NOT NULL,
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_anh_san_pham FOREIGN KEY (id_san_pham) REFERENCES san_pham(id) ON DELETE CASCADE
);
GO

-- 9. Bảng khuyến mãi (áp dụng theo mẫu áo)
CREATE TABLE khuyen_mai (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_khuyen_mai VARCHAR(50) NOT NULL UNIQUE,
    ten_khuyen_mai NVARCHAR(255) NOT NULL,
    so_tien_giam DECIMAL(12,2) DEFAULT 0,
    phan_tram_giam INT DEFAULT 0,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    mo_ta NVARCHAR(255),
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 10. Bảng sản phẩm chi tiết (Biến thể tồn kho từng màu / size)
CREATE TABLE san_pham_chi_tiet (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_san_pham_chi_tiet VARCHAR(50) NOT NULL UNIQUE,
    id_san_pham INT NOT NULL,
    id_mau_sac INT NOT NULL,
    id_kich_thuoc INT NOT NULL,
    id_khuyen_mai INT NULL,
    gia_ban_ban_dau DECIMAL(12,2) NOT NULL,
    gia_ban DECIMAL(12,2) NOT NULL,
    so_luong_ton INT NOT NULL DEFAULT 0,
    trang_thai BIT DEFAULT 1,
    CONSTRAINT fk_spct_sp FOREIGN KEY (id_san_pham) REFERENCES san_pham(id),
    CONSTRAINT fk_spct_mau FOREIGN KEY (id_mau_sac) REFERENCES mau_sac(id),
    CONSTRAINT fk_spct_size FOREIGN KEY (id_kich_thuoc) REFERENCES kich_thuoc(id),
    CONSTRAINT fk_spct_km FOREIGN KEY (id_khuyen_mai) REFERENCES khuyen_mai(id)
);
GO

-- 11. Bảng phiếu giảm giá / Voucher (áp dụng theo hóa đơn)
CREATE TABLE giam_gia (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_giam_gia VARCHAR(50) NOT NULL UNIQUE,
    ten_giam_gia NVARCHAR(255) NOT NULL,
    loai_giam_gia INT DEFAULT 1, -- 1: Giảm %, 2: Giảm tiền mặt
    gia_tri_giam DECIMAL(12,2) NOT NULL,
    toi_da_giam_gia DECIMAL(12,2) DEFAULT 0,
    gia_tri_toi_thieu DECIMAL(12,2) DEFAULT 0,
    so_luong INT NOT NULL DEFAULT 100,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    trang_thai BIT DEFAULT 1,
    mo_ta NVARCHAR(255),
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 12. Bảng vai trò
CREATE TABLE vai_tro (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ten_vai_tro NVARCHAR(100) NOT NULL UNIQUE
);
GO

-- 13. Bảng nhân viên
CREATE TABLE nhan_vien (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_nhan_vien VARCHAR(50) NOT NULL UNIQUE,
    id_vai_tro INT NOT NULL,
    ten_dang_nhap VARCHAR(255) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    ho_va_ten NVARCHAR(255) NOT NULL,
    gioi_tinh BIT DEFAULT 1,
    ngay_sinh DATE,
    email NVARCHAR(255),
    so_dien_thoai NVARCHAR(20),
    dia_chi NVARCHAR(255),
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_nv_vaitro FOREIGN KEY (id_vai_tro) REFERENCES vai_tro(id)
);
GO

-- 14. Bảng khách hàng
CREATE TABLE khach_hang (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_khach_hang VARCHAR(50) NOT NULL UNIQUE,
    ho_ten NVARCHAR(255) NOT NULL,
    gioi_tinh BIT DEFAULT 1,
    ngay_sinh DATE,
    email NVARCHAR(255),
    so_dien_thoai NVARCHAR(20),
    mat_khau VARCHAR(255),
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 15. Bảng địa chỉ nhận hàng
CREATE TABLE dia_chi (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_khach_hang INT NOT NULL,
    duong NVARCHAR(100),
    xa_phuong NVARCHAR(100),
    quan_huyen NVARCHAR(100),
    tinh_thanh_pho NVARCHAR(100),
    mac_dinh BIT DEFAULT 0,
    CONSTRAINT fk_dc_khach FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id) ON DELETE CASCADE
);
GO

-- 16. Bảng giỏ hàng chi tiết
CREATE TABLE gio_hang_chi_tiet (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_khach_hang INT NOT NULL,
    id_ao_dai_chi_tiet INT NOT NULL,
    so_luong INT NOT NULL DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_gh_khach FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_gh_spct FOREIGN KEY (id_ao_dai_chi_tiet) REFERENCES san_pham_chi_tiet(id)
);
GO

-- 17. Bảng hóa đơn
CREATE TABLE hoa_don (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_hoa_don VARCHAR(50) NOT NULL UNIQUE,
    id_khach_hang INT NULL,
    id_nhan_vien INT NULL,
    id_giam_gia INT NULL,
    tong_tien DECIMAL(12,2) NOT NULL DEFAULT 0,
    tien_truoc_giam DECIMAL(12,2) DEFAULT 0,
    phi_giao_hang DECIMAL(12,2) DEFAULT 0,
    phuong_thuc_thanh_toan BIT DEFAULT 0, -- 0: COD, 1: Chuyển khoản
    hinh_thuc_mua_hang BIT DEFAULT 0,     -- 0: Online, 1: Tại quầy POS
    dia_chi_giao_hang NVARCHAR(255),
    ghi_chu NVARCHAR(255),
    trang_thai NVARCHAR(255) DEFAULT N'Chờ xác nhận',
    ngay_tao DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_hd_khach FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id),
    CONSTRAINT fk_hd_nhanvien FOREIGN KEY (id_nhan_vien) REFERENCES nhan_vien(id),
    CONSTRAINT fk_hd_giamgia FOREIGN KEY (id_giam_gia) REFERENCES giam_gia(id)
);
GO

-- 18. Bảng hóa đơn chi tiết
CREATE TABLE hoa_don_chi_tiet (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ma_hoa_don_chi_tiet VARCHAR(50) NULL,
    id_hoa_don INT NOT NULL,
    id_ao_dai_chi_tiet INT NOT NULL,
    so_luong INT NOT NULL DEFAULT 1,
    don_gia DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_hdct_hd FOREIGN KEY (id_hoa_don) REFERENCES hoa_don(id) ON DELETE CASCADE,
    CONSTRAINT fk_hdct_spct FOREIGN KEY (id_ao_dai_chi_tiet) REFERENCES san_pham_chi_tiet(id)
);
GO

-- ===============================================================
-- CHÈN DỮ LIỆU MẪU BAN ĐẦU (SEED DATA)
-- ===============================================================

-- 1. Vai trò
INSERT INTO vai_tro (ten_vai_tro) VALUES 
(N'ADMIN'), 
(N'STAFF');

-- 2. Nhân viên
INSERT INTO nhan_vien (ma_nhan_vien, id_vai_tro, ten_dang_nhap, mat_khau, ho_va_ten, gioi_tinh, ngay_sinh, email, so_dien_thoai, dia_chi, trang_thai) VALUES
('NV01', 1, 'admin', '123456', N'Quản Trị Viên', 1, '2000-01-01', 'admin@luonvuituoi.vn', '0988888888', N'Hà Nội', 1),
('NV02', 2, 'staff01', '123456', N'Trần Thị Thu Ngân', 0, '2002-05-15', 'thungan@luonvuituoi.vn', '0977777777', N'Hà Nội', 1);

-- 3. Khách hàng
INSERT INTO khach_hang (ma_khach_hang, ho_ten, gioi_tinh, ngay_sinh, email, so_dien_thoai, mat_khau, trang_thai) VALUES
('KH01', N'Nguyễn Văn An', 1, '2003-08-20', 'an.nguyen@gmail.com', '0912345678', '123456', 1),
('KH02', N'Lê Thị Mai', 0, '2004-12-10', 'mai.le@gmail.com', '0923456789', '123456', 1);

-- 4. Địa chỉ khách
INSERT INTO dia_chi (id_khach_hang, duong, xa_phuong, quan_huyen, tinh_thanh_pho, mac_dinh) VALUES
(1, N'Số 12 ngõ 45 Cầu Giấy', N'Dịch Vọng', N'Cầu Giấy', N'Hà Nội', 1),
(2, N'Số 88 đường Nguyễn Trãi', N'Thanh Xuân Bắc', N'Thanh Xuân', N'Hà Nội', 1);

-- 5. Loại sản phẩm
INSERT INTO loai_san_pham (ten_loai_san_pham, mo_ta, trang_thai) VALUES
(N'Áo thun T-Shirt', N'Áo thun phong cách trẻ trung, thoáng mát', 1),
(N'Áo Polo', N'Áo polo cổ bẻ lịch sự, thanh lịch', 1),
(N'Áo sơ mi', N'Sơ mi công sở chống nhăn, phom chuẩn', 1),
(N'Áo Hoodie & Sweater', N'Áo nỉ ấm áp thu đông', 1),
(N'Áo khoác Jacket', N'Áo khoác dù cản gió, chống nước nhẹ', 1);

-- 6. Nhà cung cấp / Thương hiệu
INSERT INTO nha_cung_cap (ten_nha_cung_cap, mo_ta, trang_thai) VALUES
(N'Coolmate', N'Thương hiệu thời trang nam tối giản', 1),
(N'Teelab', N'Thương hiệu thời trang gen Z năng động', 1),
(N'Dirty Coins', N'Thương hiệu thời trang streetwear', 1),
(N'Routine', N'Thời trang phong cách hiện đại thanh lịch', 1);

-- 7. Chất liệu
INSERT INTO chat_lieu (ten_chat_lieu, mo_ta, trang_thai) VALUES
(N'Cotton 100%', N'Thấm hút mồ hôi tối đa, mềm mại', 1),
(N'Cotton Compact', N'Sợi dệt mịn, chống bai xù', 1),
(N'Vải Pique (Cá sấu)', N'Dày dặn, đứng phom polo', 1),
(N'Vải Lụa Chống Nhăn', N'Mịn mát, thoáng khí', 1);

-- 8. Cổ áo
INSERT INTO co_ao (ten_co_ao, mo_ta, trang_thai) VALUES
(N'Cổ tròn', N'Cổ bo thun co giãn thoải mái', 1),
(N'Cổ bẻ Polo', N'Cổ dệt bo phối khuy cài lịch sự', 1),
(N'Cổ chữ V', N'Cổ tim trẻ trung, tôn dáng', 1),
(N'Cổ trụ (Cổ tàu)', N'Cổ dựng 2cm phong cách cổ điển', 1);

-- 9. Màu sắc
INSERT INTO mau_sac (ten_mau_sac, mo_ta, trang_thai) VALUES
(N'Trắng', N'Màu trắng tinh khiết', 1),
(N'Đen', N'Màu đen huyền bí basic', 1),
(N'Xanh Navy', N'Xanh than thanh lịch', 1),
(N'Xám Tiêu', N'Xám lông chuột trẻ trung', 1),
(N'Màu Be', N'Màu kem be phong cách vintage', 1);

-- 10. Kích thước (Size)
INSERT INTO kich_thuoc (ten_kich_thuoc, mo_ta, trang_thai) VALUES
(N'S', N'Dưới 55kg', 1),
(N'M', N'Từ 55kg - 65kg', 1),
(N'L', N'Từ 65kg - 75kg', 1),
(N'XL', N'Từ 75kg - 85kg', 1),
(N'XXL', N'Trên 85kg', 1);

-- 11. Sản phẩm (Mẫu áo)
INSERT INTO san_pham (ma_san_pham, ten_san_pham, mo_ta, id_loai_san_pham, id_nha_cung_cap, id_chat_lieu, id_co_ao, trang_thai) VALUES
('AT01', N'Áo Thun Cotton Basic Luôn Vui Tươi', N'Áo thun cotton 100% 250gsm co giãn 4 chiều, thấm hút cực tốt, in chữ Luôn Vui Tươi trước ngực.', 1, 1, 1, 1, 1),
('AP01', N'Áo Polo Pique Cổ Bẻ Form Regular', N'Áo polo vải cá sấu mắt chim thoáng khí, bo cổ dệt tinh tế, phù hợp đi làm, đi chơi.', 2, 4, 3, 2, 1),
('SM01', N'Áo Sơ Mi Dài Tay Chống Nhăn', N'Chất vải lụa pha sợi modal cao cấp, không nhăn sau khi giặt, form slimfit tôn dáng.', 3, 4, 4, 2, 1);

-- 12. Hình ảnh sản phẩm
INSERT INTO anh (id_san_pham, anh_url, trang_thai) VALUES
(1, 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600', 1),
(1, 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600', 1),
(2, 'https://images.unsplash.com/photo-1625910513413-7e2300b8e8f7?w=600', 1),
(3, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600', 1);

-- 13. Khuyến mãi theo sản phẩm
INSERT INTO khuyen_mai (ma_khuyen_mai, ten_khuyen_mai, so_tien_giam, phan_tram_giam, ngay_bat_dau, ngay_ket_thuc, mo_ta, trang_thai) VALUES
('KM_HE2026', N'Chào Hè Luôn Vui Tươi - Giảm 15%', 0, 15, '2026-05-01', '2026-12-31', N'Giảm giá 15% cho dòng áo thun mùa hè', 1);

-- 14. Sản phẩm chi tiết (Biến thể áo)
INSERT INTO san_pham_chi_tiet (ma_san_pham_chi_tiet, id_san_pham, id_mau_sac, id_kich_thuoc, id_khuyen_mai, gia_ban_ban_dau, gia_ban, so_luong_ton, trang_thai) VALUES
('AT01-TRANG-M', 1, 1, 2, 1, 199000, 169150, 50, 1),
('AT01-TRANG-L', 1, 1, 3, 1, 199000, 169150, 40, 1),
('AT01-DEN-M',   1, 2, 2, 1, 199000, 169150, 60, 1),
('AT01-DEN-L',   1, 2, 3, 1, 199000, 169150, 35, 1),
('AP01-NAVY-M',  2, 3, 2, NULL, 299000, 299000, 30, 1),
('AP01-NAVY-L',  2, 3, 3, NULL, 299000, 299000, 25, 1),
('SM01-TRANG-L', 3, 1, 3, NULL, 350000, 350000, 20, 1);

-- 15. Phiếu giảm giá Voucher
INSERT INTO giam_gia (ma_giam_gia, ten_giam_gia, loai_giam_gia, gia_tri_giam, toi_da_giam_gia, gia_tri_toi_thieu, so_luong, ngay_bat_dau, ngay_ket_thuc, trang_thai, mo_ta) VALUES
('VUITUOI10', N'Voucher Luôn Vui Tươi 10%', 1, 10, 50000, 200000, 100, '2026-01-01', '2026-12-31', 1, N'Giảm 10% tối đa 50k cho đơn từ 200k'),
('GIAM30K',   N'Giảm trực tiếp 30.000đ',  2, 30000, 30000, 300000, 50,  '2026-01-01', '2026-12-31', 1, N'Giảm 30.000đ cho đơn từ 300k');

-- 16. Giỏ hàng mẫu
INSERT INTO gio_hang_chi_tiet (id_khach_hang, id_ao_dai_chi_tiet, so_luong) VALUES
(1, 1, 2),
(1, 5, 1);

-- 17. Hóa đơn mẫu
INSERT INTO hoa_don (ma_hoa_don, id_khach_hang, id_nhan_vien, id_giam_gia, tong_tien, tien_truoc_giam, phi_giao_hang, phuong_thuc_thanh_toan, hinh_thuc_mua_hang, dia_chi_giao_hang, ghi_chu, trang_thai) VALUES
('HD-20261011-001', 1, NULL, 1, 334470, 338300, 30000, 0, 0, N'Số 12 ngõ 45 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', N'Đang giao'),
('HD-20261011-002', 2, 2,    NULL, 299000, 299000, 0,     0, 1, N'Mua tại quầy shop', N'Khách mua trực tiếp', N'Đã hoàn thành');

-- 18. Hóa đơn chi tiết mẫu
INSERT INTO hoa_don_chi_tiet (ma_hoa_don_chi_tiet, id_hoa_don, id_ao_dai_chi_tiet, so_luong, don_gia) VALUES
('HDCT01', 1, 1, 2, 169150),
('HDCT02', 2, 5, 1, 299000);
GO
