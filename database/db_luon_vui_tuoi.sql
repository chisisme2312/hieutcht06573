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


-- ===============================================================
-- DU LIEU TOAN BO HE THONG (EXPORT LIVE DATA)
-- ===============================================================

-- -------------------------------------------------------------
-- Bang: vai_tro (2 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [vai_tro] ON;
GO
INSERT INTO [vai_tro] ([id], [ten_vai_tro]) VALUES
  (1, N'ADMIN'),
  (2, N'STAFF');
SET IDENTITY_INSERT [vai_tro] OFF;
GO

-- -------------------------------------------------------------
-- Bang: nhan_vien (2 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [nhan_vien] ON;
GO
INSERT INTO [nhan_vien] ([id], [ma_nhan_vien], [id_vai_tro], [ten_dang_nhap], [mat_khau], [ho_va_ten], [gioi_tinh], [ngay_sinh], [email], [so_dien_thoai], [dia_chi], [trang_thai], [ngay_tao]) VALUES
  (1, N'NV01', 1, N'admin', N'123456', N'Quản Trị Viên', 1, '2000-01-01 00:00:00', N'admin@luonvuituoi.vn', N'0988888888', N'Hà Nội', 1, '2026-10-11 12:49:37'),
  (2, N'NV02', 2, N'staff01', N'123456', N'Trần Thị Thu Ngân', 0, '2002-05-15 00:00:00', N'thungan@luonvuituoi.vn', N'0977777777', N'Hà Nội', 1, '2026-10-11 12:49:37');
SET IDENTITY_INSERT [nhan_vien] OFF;
GO

-- -------------------------------------------------------------
-- Bang: khach_hang (5 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [khach_hang] ON;
GO
INSERT INTO [khach_hang] ([id], [ma_khach_hang], [ho_ten], [gioi_tinh], [ngay_sinh], [email], [so_dien_thoai], [mat_khau], [trang_thai], [ngay_tao]) VALUES
  (1, N'KH01', N'Nguyễn Văn An', 1, '2003-08-20 00:00:00', N'an.nguyen@gmail.com', N'0912345678', N'123456', 1, '2026-10-11 12:49:37'),
  (2, N'KH02', N'Lê Thị Mai', 0, '2004-12-10 00:00:00', N'mai.le@gmail.com', N'0923456789', N'123456', 1, '2026-10-11 12:49:37'),
  (3, N'KH03', N'Trần Hoàng Nam', 1, '2001-05-12 00:00:00', N'nam.tran@gmail.com', N'0981122334', N'123456', 1, '2026-09-22 15:46:00'),
  (4, N'KH04', N'Phạm Quốc Bảo', 1, '1999-08-24 00:00:00', N'bao.pham@gmail.com', N'0978998877', N'123456', 1, '2026-10-02 15:46:00'),
  (5, N'KH05', N'Vũ Thu Thảo', 0, '2003-11-15 00:00:00', N'thao.vu@gmail.com', N'0965443322', N'123456', 1, '2026-10-12 15:46:00');
SET IDENTITY_INSERT [khach_hang] OFF;
GO

-- -------------------------------------------------------------
-- Bang: dia_chi (2 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [dia_chi] ON;
GO
INSERT INTO [dia_chi] ([id], [id_khach_hang], [duong], [xa_phuong], [quan_huyen], [tinh_thanh_pho], [mac_dinh]) VALUES
  (1, 1, N'Số 12 ngõ 45 Cầu Giấy', N'Dịch Vọng', N'Cầu Giấy', N'Hà Nội', 1),
  (2, 2, N'Số 88 đường Nguyễn Trãi', N'Thanh Xuân Bắc', N'Thanh Xuân', N'Hà Nội', 1);
SET IDENTITY_INSERT [dia_chi] OFF;
GO

-- -------------------------------------------------------------
-- Bang: loai_san_pham (6 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [loai_san_pham] ON;
GO
INSERT INTO [loai_san_pham] ([id], [ten_loai_san_pham], [trang_thai], [mo_ta], [ngay_tao]) VALUES
  (1, N'Áo thun T-Shirt', 1, N'Áo thun phong cách trẻ trung, thoáng mát', '2026-10-11 12:49:37'),
  (2, N'Áo Polo', 1, N'Áo polo cổ bẻ lịch sự, thanh lịch', '2026-10-11 12:49:37'),
  (3, N'Áo sơ mi', 1, N'Sơ mi công sở chống nhăn, phom chuẩn', '2026-10-11 12:49:37'),
  (4, N'Áo Hoodie', 1, N'Áo nỉ ấm áp thu đông có mũ', NULL),
  (5, N'Áo khoác Jacket', 1, N'Áo khoác dù cản gió, chống nước nhẹ', '2026-10-11 12:49:37'),
  (6, N'Áo Sweater', 1, N'Áo nỉ ấm áp thu đông', '2026-10-22 14:49:15');
SET IDENTITY_INSERT [loai_san_pham] OFF;
GO

-- -------------------------------------------------------------
-- Bang: nha_cung_cap (4 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [nha_cung_cap] ON;
GO
INSERT INTO [nha_cung_cap] ([id], [ten_nha_cung_cap], [trang_thai], [mo_ta], [ngay_tao]) VALUES
  (1, N'Coolmate', 1, N'Thương hiệu thời trang nam tối giản', '2026-10-11 12:49:37'),
  (2, N'Teelab', 1, N'Thương hiệu thời trang gen Z năng động', '2026-10-11 12:49:37'),
  (3, N'Dirty Coins', 1, N'Thương hiệu thời trang streetwear', '2026-10-11 12:49:37'),
  (4, N'Routine', 1, N'Thời trang phong cách hiện đại thanh lịch', '2026-10-11 12:49:37');
SET IDENTITY_INSERT [nha_cung_cap] OFF;
GO

-- -------------------------------------------------------------
-- Bang: chat_lieu (4 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [chat_lieu] ON;
GO
INSERT INTO [chat_lieu] ([id], [ten_chat_lieu], [trang_thai], [mo_ta], [ngay_tao]) VALUES
  (1, N'Cotton 100%', 1, N'Thấm hút mồ hôi tối đa, mềm mại', '2026-10-11 12:49:37'),
  (2, N'Cotton Compact', 1, N'Sợi dệt mịn, chống bai xù', '2026-10-11 12:49:37'),
  (3, N'Vải Pique (Cá sấu)', 1, N'Dày dặn, đứng phom polo', '2026-10-11 12:49:37'),
  (4, N'Vải Lụa Chống Nhăn', 1, N'Mịn mát, thoáng khí', '2026-10-11 12:49:37');
SET IDENTITY_INSERT [chat_lieu] OFF;
GO

-- -------------------------------------------------------------
-- Bang: co_ao (4 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [co_ao] ON;
GO
INSERT INTO [co_ao] ([id], [ten_co_ao], [trang_thai], [mo_ta], [ngay_tao]) VALUES
  (1, N'Cổ tròn', 1, N'Cổ bo thun co giãn thoải mái', '2026-10-11 12:49:37'),
  (2, N'Cổ bẻ Polo', 1, N'Cổ dệt bo phối khuy cài lịch sự', '2026-10-11 12:49:37'),
  (3, N'Cổ chữ V', 1, N'Cổ tim trẻ trung, tôn dáng', '2026-10-11 12:49:37'),
  (4, N'Cổ trụ (Cổ tàu)', 1, N'Cổ dựng 2cm phong cách cổ điển', '2026-10-11 12:49:37');
SET IDENTITY_INSERT [co_ao] OFF;
GO

-- -------------------------------------------------------------
-- Bang: mau_sac (5 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [mau_sac] ON;
GO
INSERT INTO [mau_sac] ([id], [ten_mau_sac], [trang_thai], [mo_ta], [ngay_tao]) VALUES
  (1, N'Trắng', 1, N'Màu trắng tinh khiết', '2026-10-11 12:49:37'),
  (2, N'Đen', 1, N'Màu đen huyền bí basic', '2026-10-11 12:49:37'),
  (3, N'Xanh Navy', 1, N'Xanh than thanh lịch', '2026-10-11 12:49:37'),
  (4, N'Xám Tiêu', 1, N'Xám lông chuột trẻ trung', '2026-10-11 12:49:37'),
  (6, N'cầu vồng', 1, N'gay', '2026-10-22 14:49:55');
SET IDENTITY_INSERT [mau_sac] OFF;
GO

-- -------------------------------------------------------------
-- Bang: kich_thuoc (6 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [kich_thuoc] ON;
GO
INSERT INTO [kich_thuoc] ([id], [ten_kich_thuoc], [trang_thai], [mo_ta], [ngay_tao]) VALUES
  (1, N'S', 1, N'Dưới 55kg', NULL),
  (2, N'M', 1, N'Từ 55kg - 65kg', '2026-10-11 12:49:37'),
  (3, N'L', 1, N'Từ 65kg - 75kg', '2026-10-11 12:49:37'),
  (4, N'XL', 1, N'Từ 75kg - 85kg', '2026-10-11 12:49:37'),
  (5, N'XXL', 1, N'Trên 85kg', '2026-10-11 12:49:37'),
  (6, N'4XL', 1, N'Trên 100kg', '2026-10-22 14:52:12');
SET IDENTITY_INSERT [kich_thuoc] OFF;
GO

-- -------------------------------------------------------------
-- Bang: khuyen_mai (1 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [khuyen_mai] ON;
GO
INSERT INTO [khuyen_mai] ([id], [ma_khuyen_mai], [ten_khuyen_mai], [so_tien_giam], [phan_tram_giam], [ngay_bat_dau], [ngay_ket_thuc], [mo_ta], [trang_thai], [ngay_tao]) VALUES
  (1, N'KM_HE2026', N'Chào Hè Luôn Vui Tươi - Giảm 15%', 0.00, 15, '2026-05-01 00:00:00', '2026-12-31 00:00:00', N'Giảm giá 15% cho dòng áo thun mùa hè', 1, '2026-10-11 12:49:37');
SET IDENTITY_INSERT [khuyen_mai] OFF;
GO

-- -------------------------------------------------------------
-- Bang: san_pham (25 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [san_pham] ON;
GO
INSERT INTO [san_pham] ([id], [ma_san_pham], [ten_san_pham], [mo_ta], [id_loai_san_pham], [id_nha_cung_cap], [id_chat_lieu], [id_co_ao], [trang_thai], [ngay_tao]) VALUES
  (1, N'AT01', N'Áo Thun Cotton Basic Luôn Vui Tươi', N'Chất liệu cotton 100% co giãn 4 chiều mềm mịn, thấm hút mồ hôi tối đa.', 1, 1, 1, 1, 1, '2026-10-11 12:49:37'),
  (2, N'AP01', N'Áo Polo Pique Cổ Bẻ Form Regular', N'Vải Pique mắt chim cao cấp dệt tổ ong, đứng dáng, chuẩn phong cách quý ông.', 2, 4, 3, 2, 1, '2026-10-11 12:49:37'),
  (3, N'SM01', N'Áo Sơ Mi Dài Tay Chống Nhăn', N'Chất liệu lụa pha modal chống nhăn tuyệt đối, thoáng mát, form dáng thanh lịch.', 3, 4, 4, 2, 1, '2026-10-11 12:49:37'),
  (5, N'asd', N'ao thun ', N'', 1, 1, 1, 1, 1, '2026-10-21 16:42:47'),
  (6, N'AT02', N'Áo Thun Oversize Unisex In Hình Retro', N'Chất liệu cotton 100% thoáng mát, form rộng unisex cá tính.', 1, 2, 1, 1, 1, '2026-10-22 09:49:53'),
  (7, N'AT03', N'Áo Thun Cổ Tròn Cotton Compact Kháng Khuẩn', N'Sợi Compact chống xù lông, công nghệ kháng khuẩn khử mùi vượt trội.', 1, 1, 2, 1, 1, '2026-10-22 09:49:53'),
  (8, N'AT04', N'Áo Thun Tay Lỡ Streetwear Form Rộng', N'Phong cách đường phố trẻ trung năng động, dễ dàng phối đồ dạo phố.', 1, 3, 1, 1, 1, '2026-10-22 09:49:53'),
  (9, N'AT05', N'Áo Thun Raglan Phối Màu Thể Thao Nam', N'Thiết kế tay raglan phong cách bóng chày cá tính, co giãn nhẹ.', 1, 4, 1, 1, 1, '2026-10-22 09:49:53'),
  (10, N'AP02', N'Áo Polo Dệt Tổ Ong Thấm Hút Mồ Hôi', N'Mặt vải dệt tổ ong thông thoáng, thấm hút cực nhanh, chuẩn form công sở.', 2, 4, 3, 2, 1, '2026-10-22 09:49:53'),
  (11, N'AP03', N'Áo Polo Cổ Bo Phối Viền Tương Phản', N'Bo cổ và tay dệt sọc thể thao tinh tế, lịch sự trong mọi hoàn cảnh.', 2, 1, 3, 2, 1, '2026-10-22 09:49:53'),
  (12, N'AP04', N'Áo Polo Thể Thao Co Giãn 4 Chiều Active', N'Vải co giãn thể thao cao cấp, chuyên dụng vận động ngoài trời và golf.', 2, 2, 2, 2, 1, '2026-10-22 09:49:53'),
  (13, N'AP05', N'Áo Polo Cổ Khóa Kéo Zipper Hiện Đại', N'Thiết kế khóa kéo kim loại thay thế hàng nút truyền thống sang trọng.', 2, 3, 3, 2, 1, '2026-10-22 09:49:53'),
  (14, N'SM02', N'Áo Sơ Mi Oxford Dài Tay Form Slimfit', N'Vải Oxford dệt dày dặn, đứng dáng, phù hợp đi làm và gặp đối tác.', 3, 4, 4, 2, 1, '2026-10-22 09:49:53'),
  (15, N'SM03', N'Áo Sơ Mi Cổ Trụ Vải Modal Mềm Mát', N'Chất liệu sợi gỗ sồi modal siêu mềm mịn, chống nhăn tự nhiên.', 3, 1, 4, 4, 1, '2026-10-22 09:49:53'),
  (16, N'SM04', N'Áo Sơ Mi Tay Ngắn Họa Tiết Kẻ Caro', N'Họa tiết caro thanh lịch, form regular fit thoải mái cả ngày hè.', 3, 2, 4, 2, 1, '2026-10-22 09:49:53'),
  (17, N'SM05', N'Áo Sơ Mi Linen Nam Phong Cách Mộc', N'Chất đũi linen tự nhiên thoáng mát, mang lại cảm giác thư thái.', 3, 4, 1, 2, 1, '2026-10-22 09:49:53'),
  (18, N'HD01', N'Áo Hoodie Nỉ Bông Unisex Phom Rộng', N'Nỉ bông dày dặn ấm áp, có mũ trùm đôi và túi kangaroo tiện dụng.', 4, 3, 1, 1, 1, '2026-10-22 09:49:53'),
  (19, N'HD02', N'Áo Sweater Dài Tay Thêu Chữ Nổi Basic', N'Bo gấu dệt kim co giãn, thêu logo sắc nét ngực áo trẻ trung.', 4, 2, 1, 1, 1, '2026-10-22 09:49:53'),
  (20, N'HD03', N'Áo Hoodie Zip Có Khóa Kéo Cao Cấp', N'Khóa kéo 2 chiều trơn tru, dễ dàng biến tấu phong cách layer ấm áp.', 4, 3, 1, 1, 1, '2026-10-22 09:49:53'),
  (21, N'JK01', N'Áo Khoác Bomber Lót Dù 2 Lớp Cản Gió', N'Lớp dù chống thấm nước nhẹ, lót trong giữ nhiệt ấm áp mùa thu đông.', 5, 4, 1, 1, 1, '2026-10-22 09:49:53'),
  (22, N'JK02', N'Áo Khoác Gió Thể Thao Chống Nước', N'Gấp gọn tiện lợi, chuyên dụng cho đi phượt và hoạt động thể thao.', 5, 1, 2, 1, 1, '2026-10-22 09:49:53'),
  (23, N'JK03', N'Áo Khoác Denim Bò Cổ Bẻ Bụi Bặm', N'Chất jean wash bền màu, form suông khỏe khoắn đậm chất nam tính.', 5, 3, 1, 1, 0, '2026-10-22 09:49:53'),
  (26, N'bbb', N'ao thun dep trai', N'aaaa', 5, 1, 1, 1, 1, '2026-10-22 14:55:13'),
  (29, N'bbbasd', N'ao thun ádsd', N'aaaaa', 3, 2, 2, 1, 1, '2026-10-22 15:06:24'),
  (30, N'TGB', N'áo TBG', N'TBG', 2, 1, 2, 1, 1, '2026-10-24 19:19:43');
SET IDENTITY_INSERT [san_pham] OFF;
GO

-- -------------------------------------------------------------
-- Bang: anh (21 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [anh] ON;
GO
INSERT INTO [anh] ([id], [id_san_pham], [anh_url], [trang_thai], [ngay_tao]) VALUES
  (2, 1, N'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600', 1, '2026-10-11 12:49:37'),
  (3, 2, N'https://images.unsplash.com/photo-1625910513413-7e2300b8e8f7?w=600', 1, '2026-10-11 12:49:37'),
  (4, 3, N'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600', 1, '2026-10-11 12:49:37'),
  (7, 6, N'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600', 1, '2026-10-22 09:49:53'),
  (8, 7, N'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600', 1, '2026-10-22 09:49:53'),
  (9, 8, N'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=600', 1, '2026-10-22 09:49:53'),
  (10, 9, N'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=600', 1, '2026-10-22 09:49:53'),
  (11, 10, N'https://images.unsplash.com/photo-1625910513413-7e2300b8e8f7?w=600', 1, '2026-10-22 09:49:53'),
  (12, 11, N'https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600', 1, '2026-10-22 09:49:53'),
  (13, 12, N'https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=600', 1, '2026-10-22 09:49:53'),
  (14, 13, N'https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=600', 1, '2026-10-22 09:49:53'),
  (15, 14, N'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600', 1, '2026-10-22 09:49:53'),
  (16, 15, N'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600', 1, '2026-10-22 09:49:53'),
  (17, 16, N'https://images.unsplash.com/photo-1607345366928-199ea26cfe3e?w=600', 1, '2026-10-22 09:49:53'),
  (18, 17, N'https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=600', 1, '2026-10-22 09:49:53'),
  (19, 18, N'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=600', 1, '2026-10-22 09:49:53'),
  (20, 19, N'https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=600', 1, '2026-10-22 09:49:53'),
  (21, 20, N'https://images.unsplash.com/photo-1509967419530-da38b4704bc6?w=600', 1, '2026-10-22 09:49:53'),
  (22, 21, N'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=600', 1, '2026-10-22 09:49:53'),
  (23, 22, N'https://images.unsplash.com/photo-1544441893-675973e31985?w=600', 1, '2026-10-22 09:49:53'),
  (24, 23, N'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=600', 1, '2026-10-22 09:49:53');
SET IDENTITY_INSERT [anh] OFF;
GO

-- -------------------------------------------------------------
-- Bang: san_pham_chi_tiet (208 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [san_pham_chi_tiet] ON;
GO
INSERT INTO [san_pham_chi_tiet] ([id], [ma_san_pham_chi_tiet], [id_san_pham], [id_mau_sac], [id_kich_thuoc], [id_khuyen_mai], [gia_ban_ban_dau], [gia_ban], [so_luong_ton], [trang_thai]) VALUES
  (1, N'AT01-1-2', 1, 1, 2, 1, 199000.00, 160000.00, 4, 1),
  (2, N'AT01-TRANG-L', 1, 1, 3, 1, 199000.00, 169150.00, 2, 1),
  (3, N'AT01-DEN-M', 1, 2, 2, 1, 199000.00, 169150.00, 60, 1),
  (4, N'AT01-DEN-L', 1, 2, 3, 1, 199000.00, 169150.00, 35, 1),
  (5, N'AP01-NAVY-M', 2, 3, 2, NULL, 299000.00, 299000.00, 6, 1),
  (6, N'AP01-NAVY-L', 2, 3, 3, NULL, 299000.00, 299000.00, 25, 1),
  (7, N'SM01-TRANG-L', 3, 1, 3, NULL, 350000.00, 350000.00, 20, 1),
  (9, N'AT01-3-5', 1, 3, 5, NULL, 200000.00, 200000.00, 100, 1),
  (10, N'asd-1-1', 5, 1, 1, NULL, 250000.00, 199000.00, 50, 1),
  (11, N'asd-1-2', 5, 1, 2, NULL, 250000.00, 199000.00, 50, 1),
  (12, N'asd-1-3', 5, 1, 3, NULL, 250000.00, 199000.00, 50, 1),
  (13, N'asd-1-4', 5, 1, 4, NULL, 250000.00, 199000.00, 50, 1),
  (14, N'asd-1-5', 5, 1, 5, NULL, 250000.00, 199000.00, 50, 1),
  (15, N'asd-2-1', 5, 2, 1, NULL, 250000.00, 199000.00, 50, 1),
  (16, N'asd-2-2', 5, 2, 2, NULL, 250000.00, 199000.00, 50, 1),
  (17, N'asd-2-3', 5, 2, 3, NULL, 250000.00, 199000.00, 50, 1),
  (18, N'asd-2-4', 5, 2, 4, NULL, 250000.00, 199000.00, 50, 1),
  (19, N'asd-2-5', 5, 2, 5, NULL, 250000.00, 199000.00, 50, 1),
  (20, N'asd-3-1', 5, 3, 1, NULL, 250000.00, 199000.00, 50, 1),
  (21, N'asd-3-2', 5, 3, 2, NULL, 250000.00, 199000.00, 50, 1),
  (22, N'asd-3-3', 5, 3, 3, NULL, 250000.00, 199000.00, 50, 1),
  (23, N'asd-3-4', 5, 3, 4, NULL, 250000.00, 199000.00, 50, 1),
  (24, N'asd-3-5', 5, 3, 5, NULL, 250000.00, 199000.00, 50, 1),
  (25, N'asd-4-1', 5, 4, 1, NULL, 250000.00, 199000.00, 50, 1),
  (26, N'asd-4-2', 5, 4, 2, NULL, 250000.00, 199000.00, 50, 1),
  (27, N'asd-4-3', 5, 4, 3, NULL, 250000.00, 199000.00, 50, 1),
  (28, N'asd-4-4', 5, 4, 4, NULL, 250000.00, 199000.00, 50, 1),
  (29, N'asd-4-5', 5, 4, 5, NULL, 250000.00, 199000.00, 50, 1),
  (30, N'AT02-TRANG-M', 6, 1, 2, NULL, 220000.00, 189000.00, 25, 1),
  (31, N'AT02-TRANG-L', 6, 1, 3, NULL, 220000.00, 189000.00, 30, 1),
  (32, N'AT02-DEN-M', 6, 2, 2, NULL, 220000.00, 189000.00, 35, 1),
  (33, N'AT02-DEN-L', 6, 2, 3, NULL, 220000.00, 189000.00, 20, 1),
  (34, N'AT02-DEN-XL', 6, 2, 4, NULL, 220000.00, 189000.00, 15, 1),
  (35, N'AT03-TRANG-M', 7, 1, 2, NULL, 199000.00, 179000.00, 25, 1),
  (36, N'AT03-TRANG-L', 7, 1, 3, NULL, 199000.00, 179000.00, 30, 1),
  (37, N'AT03-DEN-M', 7, 2, 2, NULL, 199000.00, 179000.00, 35, 1),
  (38, N'AT03-DEN-L', 7, 2, 3, NULL, 199000.00, 179000.00, 20, 1),
  (39, N'AT03-DEN-XL', 7, 2, 4, NULL, 199000.00, 179000.00, 15, 1),
  (40, N'AT04-TRANG-M', 8, 1, 2, NULL, 250000.00, 219000.00, 25, 1),
  (41, N'AT04-TRANG-L', 8, 1, 3, NULL, 250000.00, 219000.00, 30, 1),
  (42, N'AT04-DEN-M', 8, 2, 2, NULL, 250000.00, 219000.00, 35, 1),
  (43, N'AT04-DEN-L', 8, 2, 3, NULL, 250000.00, 219000.00, 20, 1),
  (44, N'AT04-DEN-XL', 8, 2, 4, NULL, 250000.00, 219000.00, 15, 1),
  (45, N'AT05-TRANG-M', 9, 1, 2, NULL, 199000.00, 169000.00, 25, 1),
  (46, N'AT05-TRANG-L', 9, 1, 3, NULL, 199000.00, 169000.00, 30, 1),
  (47, N'AT05-DEN-M', 9, 2, 2, NULL, 199000.00, 169000.00, 35, 1),
  (48, N'AT05-DEN-L', 9, 2, 3, NULL, 199000.00, 169000.00, 20, 1),
  (49, N'AT05-DEN-XL', 9, 2, 4, NULL, 199000.00, 169000.00, 15, 1),
  (50, N'AP02-TRANG-M', 10, 1, 2, NULL, 320000.00, 289000.00, 25, 1),
  (51, N'AP02-TRANG-L', 10, 1, 3, NULL, 320000.00, 289000.00, 30, 1);
INSERT INTO [san_pham_chi_tiet] ([id], [ma_san_pham_chi_tiet], [id_san_pham], [id_mau_sac], [id_kich_thuoc], [id_khuyen_mai], [gia_ban_ban_dau], [gia_ban], [so_luong_ton], [trang_thai]) VALUES
  (52, N'AP02-DEN-M', 10, 2, 2, NULL, 320000.00, 289000.00, 35, 1),
  (53, N'AP02-DEN-L', 10, 2, 3, NULL, 320000.00, 289000.00, 20, 1),
  (54, N'AP02-DEN-XL', 10, 2, 4, NULL, 320000.00, 289000.00, 15, 1),
  (55, N'AP03-TRANG-M', 11, 1, 2, NULL, 310000.00, 279000.00, 25, 1),
  (56, N'AP03-TRANG-L', 11, 1, 3, NULL, 310000.00, 279000.00, 30, 1),
  (57, N'AP03-DEN-M', 11, 2, 2, NULL, 310000.00, 279000.00, 35, 1),
  (58, N'AP03-DEN-L', 11, 2, 3, NULL, 310000.00, 279000.00, 20, 1),
  (59, N'AP03-DEN-XL', 11, 2, 4, NULL, 310000.00, 279000.00, 15, 1),
  (60, N'AP04-TRANG-M', 12, 1, 2, NULL, 350000.00, 299000.00, 25, 1),
  (61, N'AP04-TRANG-L', 12, 1, 3, NULL, 350000.00, 299000.00, 30, 1),
  (62, N'AP04-DEN-M', 12, 2, 2, NULL, 350000.00, 299000.00, 35, 1),
  (63, N'AP04-DEN-L', 12, 2, 3, NULL, 350000.00, 299000.00, 20, 1),
  (64, N'AP04-DEN-XL', 12, 2, 4, NULL, 350000.00, 299000.00, 15, 1),
  (65, N'AP05-TRANG-M', 13, 1, 2, NULL, 380000.00, 319000.00, 25, 1),
  (66, N'AP05-TRANG-L', 13, 1, 3, NULL, 380000.00, 319000.00, 30, 1),
  (67, N'AP05-DEN-M', 13, 2, 2, NULL, 380000.00, 319000.00, 35, 1),
  (68, N'AP05-DEN-L', 13, 2, 3, NULL, 380000.00, 319000.00, 20, 1),
  (69, N'AP05-DEN-XL', 13, 2, 4, NULL, 380000.00, 319000.00, 15, 1),
  (70, N'SM02-TRANG-M', 14, 1, 2, NULL, 399000.00, 349000.00, 25, 1),
  (71, N'SM02-TRANG-L', 14, 1, 3, NULL, 399000.00, 349000.00, 30, 1),
  (72, N'SM02-DEN-M', 14, 2, 2, NULL, 399000.00, 349000.00, 35, 1),
  (73, N'SM02-DEN-L', 14, 2, 3, NULL, 399000.00, 349000.00, 20, 1),
  (74, N'SM02-DEN-XL', 14, 2, 4, NULL, 399000.00, 349000.00, 15, 1),
  (75, N'SM03-TRANG-M', 15, 1, 2, NULL, 380000.00, 329000.00, 25, 1),
  (76, N'SM03-TRANG-L', 15, 1, 3, NULL, 380000.00, 329000.00, 30, 1),
  (77, N'SM03-DEN-M', 15, 2, 2, NULL, 380000.00, 329000.00, 35, 1),
  (78, N'SM03-DEN-L', 15, 2, 3, NULL, 380000.00, 329000.00, 20, 1),
  (79, N'SM03-DEN-XL', 15, 2, 4, NULL, 380000.00, 329000.00, 15, 1),
  (80, N'SM04-TRANG-M', 16, 1, 2, NULL, 330000.00, 289000.00, 25, 1),
  (81, N'SM04-TRANG-L', 16, 1, 3, NULL, 330000.00, 289000.00, 30, 1),
  (82, N'SM04-DEN-M', 16, 2, 2, NULL, 330000.00, 289000.00, 35, 1),
  (83, N'SM04-DEN-L', 16, 2, 3, NULL, 330000.00, 289000.00, 20, 1),
  (84, N'SM04-DEN-XL', 16, 2, 4, NULL, 330000.00, 289000.00, 15, 1),
  (85, N'SM05-TRANG-M', 17, 1, 2, NULL, 420000.00, 359000.00, 25, 1),
  (86, N'SM05-TRANG-L', 17, 1, 3, NULL, 420000.00, 359000.00, 30, 1),
  (87, N'SM05-DEN-M', 17, 2, 2, NULL, 420000.00, 359000.00, 35, 1),
  (88, N'SM05-DEN-L', 17, 2, 3, NULL, 420000.00, 359000.00, 20, 1),
  (89, N'SM05-DEN-XL', 17, 2, 4, NULL, 420000.00, 359000.00, 15, 1),
  (90, N'HD01-TRANG-M', 18, 1, 2, NULL, 450000.00, 399000.00, 25, 1),
  (91, N'HD01-TRANG-L', 18, 1, 3, NULL, 450000.00, 399000.00, 30, 1),
  (92, N'HD01-DEN-M', 18, 2, 2, NULL, 450000.00, 399000.00, 35, 1),
  (93, N'HD01-DEN-L', 18, 2, 3, NULL, 450000.00, 399000.00, 20, 1),
  (94, N'HD01-DEN-XL', 18, 2, 4, NULL, 450000.00, 399000.00, 15, 1),
  (95, N'HD02-TRANG-M', 19, 1, 2, NULL, 380000.00, 329000.00, 25, 1),
  (96, N'HD02-TRANG-L', 19, 1, 3, NULL, 380000.00, 329000.00, 30, 1),
  (97, N'HD02-DEN-M', 19, 2, 2, NULL, 380000.00, 329000.00, 35, 1),
  (98, N'HD02-DEN-L', 19, 2, 3, NULL, 380000.00, 329000.00, 20, 1),
  (99, N'HD02-DEN-XL', 19, 2, 4, NULL, 380000.00, 329000.00, 15, 1),
  (100, N'HD03-TRANG-M', 20, 1, 2, NULL, 490000.00, 429000.00, 25, 1),
  (101, N'HD03-TRANG-L', 20, 1, 3, NULL, 490000.00, 429000.00, 30, 1);
INSERT INTO [san_pham_chi_tiet] ([id], [ma_san_pham_chi_tiet], [id_san_pham], [id_mau_sac], [id_kich_thuoc], [id_khuyen_mai], [gia_ban_ban_dau], [gia_ban], [so_luong_ton], [trang_thai]) VALUES
  (102, N'HD03-DEN-M', 20, 2, 2, NULL, 490000.00, 429000.00, 35, 1),
  (103, N'HD03-DEN-L', 20, 2, 3, NULL, 490000.00, 429000.00, 20, 1),
  (104, N'HD03-DEN-XL', 20, 2, 4, NULL, 490000.00, 429000.00, 15, 1),
  (105, N'JK01-TRANG-M', 21, 1, 2, NULL, 520000.00, 450000.00, 25, 1),
  (106, N'JK01-TRANG-L', 21, 1, 3, NULL, 520000.00, 450000.00, 30, 1),
  (107, N'JK01-DEN-M', 21, 2, 2, NULL, 520000.00, 450000.00, 35, 1),
  (108, N'JK01-DEN-L', 21, 2, 3, NULL, 520000.00, 450000.00, 20, 1),
  (109, N'JK01-DEN-XL', 21, 2, 4, NULL, 520000.00, 450000.00, 15, 1),
  (110, N'JK02-TRANG-M', 22, 1, 2, NULL, 430000.00, 380000.00, 25, 1),
  (111, N'JK02-TRANG-L', 22, 1, 3, NULL, 430000.00, 380000.00, 30, 1),
  (112, N'JK02-DEN-M', 22, 2, 2, NULL, 430000.00, 380000.00, 35, 1),
  (113, N'JK02-DEN-L', 22, 2, 3, NULL, 430000.00, 380000.00, 20, 1),
  (114, N'JK02-DEN-XL', 22, 2, 4, NULL, 430000.00, 380000.00, 15, 1),
  (115, N'JK03-TRANG-M', 23, 1, 2, NULL, 580000.00, 499000.00, 25, 1),
  (116, N'JK03-TRANG-L', 23, 1, 3, NULL, 580000.00, 499000.00, 30, 1),
  (117, N'JK03-DEN-M', 23, 2, 2, NULL, 580000.00, 499000.00, 35, 1),
  (118, N'JK03-DEN-L', 23, 2, 3, NULL, 580000.00, 499000.00, 20, 1),
  (119, N'JK03-DEN-XL', 23, 2, 4, NULL, 580000.00, 499000.00, 15, 1),
  (120, N'bbb-1-1', 26, 1, 1, NULL, 250000.00, 199000.00, 50, 1),
  (121, N'bbb-1-2', 26, 1, 2, NULL, 250000.00, 199000.00, 50, 1),
  (122, N'bbb-1-3', 26, 1, 3, NULL, 250000.00, 199000.00, 50, 1),
  (123, N'bbb-1-4', 26, 1, 4, NULL, 250000.00, 199000.00, 50, 1),
  (124, N'bbb-1-5', 26, 1, 5, NULL, 250000.00, 199000.00, 50, 1),
  (125, N'bbb-1-6', 26, 1, 6, NULL, 250000.00, 199000.00, 50, 1),
  (126, N'bbb-2-1', 26, 2, 1, NULL, 250000.00, 199000.00, 50, 1),
  (127, N'bbb-2-2', 26, 2, 2, NULL, 250000.00, 199000.00, 50, 1),
  (128, N'bbb-2-3', 26, 2, 3, NULL, 250000.00, 199000.00, 50, 1),
  (129, N'bbb-2-4', 26, 2, 4, NULL, 250000.00, 199000.00, 50, 1),
  (130, N'bbb-2-5', 26, 2, 5, NULL, 250000.00, 199000.00, 50, 1),
  (131, N'bbb-2-6', 26, 2, 6, NULL, 250000.00, 199000.00, 50, 1),
  (132, N'bbb-3-1', 26, 3, 1, NULL, 250000.00, 199000.00, 50, 1),
  (133, N'bbb-3-2', 26, 3, 2, NULL, 250000.00, 199000.00, 50, 1),
  (134, N'bbb-3-3', 26, 3, 3, NULL, 250000.00, 199000.00, 50, 1),
  (135, N'bbb-3-4', 26, 3, 4, NULL, 250000.00, 199000.00, 50, 1),
  (136, N'bbb-3-5', 26, 3, 5, NULL, 250000.00, 199000.00, 50, 1),
  (137, N'bbb-3-6', 26, 3, 6, NULL, 250000.00, 199000.00, 50, 1),
  (138, N'bbb-4-1', 26, 4, 1, NULL, 250000.00, 199000.00, 50, 1),
  (139, N'bbb-4-2', 26, 4, 2, NULL, 250000.00, 199000.00, 50, 1),
  (140, N'bbb-4-3', 26, 4, 3, NULL, 250000.00, 199000.00, 50, 1),
  (141, N'bbb-4-4', 26, 4, 4, NULL, 250000.00, 199000.00, 50, 1),
  (142, N'bbb-4-5', 26, 4, 5, NULL, 250000.00, 199000.00, 50, 1),
  (143, N'bbb-4-6', 26, 4, 6, NULL, 250000.00, 199000.00, 50, 1),
  (144, N'bbb-6-1', 26, 6, 1, NULL, 250000.00, 199000.00, 50, 1),
  (145, N'bbb-6-2', 26, 6, 2, NULL, 250000.00, 199000.00, 50, 1),
  (146, N'bbb-6-3', 26, 6, 3, NULL, 250000.00, 199000.00, 50, 1),
  (147, N'bbb-6-4', 26, 6, 4, NULL, 250000.00, 199000.00, 50, 1),
  (148, N'bbb-6-5', 26, 6, 5, NULL, 250000.00, 199000.00, 50, 1),
  (149, N'bbb-6-6', 26, 6, 6, NULL, 250000.00, 199000.00, 50, 1),
  (152, N'bbbasd-1-1', 29, 1, 1, NULL, 250000.00, 199000.00, 50, 0),
  (153, N'bbbasd-1-2', 29, 1, 2, NULL, 250000.00, 199000.00, 50, 0);
INSERT INTO [san_pham_chi_tiet] ([id], [ma_san_pham_chi_tiet], [id_san_pham], [id_mau_sac], [id_kich_thuoc], [id_khuyen_mai], [gia_ban_ban_dau], [gia_ban], [so_luong_ton], [trang_thai]) VALUES
  (154, N'bbbasd-1-3', 29, 1, 3, NULL, 250000.00, 199000.00, 50, 0),
  (155, N'bbbasd-1-4', 29, 1, 4, NULL, 250000.00, 199000.00, 50, 0),
  (156, N'bbbasd-1-5', 29, 1, 5, NULL, 250000.00, 199000.00, 50, 0),
  (157, N'bbbasd-1-6', 29, 1, 6, NULL, 250000.00, 199000.00, 50, 0),
  (158, N'bbbasd-2-1', 29, 2, 1, NULL, 250000.00, 199000.00, 50, 0),
  (159, N'bbbasd-2-2', 29, 2, 2, NULL, 250000.00, 199000.00, 50, 0),
  (160, N'bbbasd-2-3', 29, 2, 3, NULL, 250000.00, 199000.00, 50, 0),
  (161, N'bbbasd-2-4', 29, 2, 4, NULL, 250000.00, 199000.00, 50, 0),
  (162, N'bbbasd-2-5', 29, 2, 5, NULL, 250000.00, 199000.00, 50, 0),
  (163, N'bbbasd-2-6', 29, 2, 6, NULL, 250000.00, 199000.00, 50, 0),
  (164, N'bbbasd-3-1', 29, 3, 1, NULL, 250000.00, 199000.00, 50, 0),
  (165, N'bbbasd-3-2', 29, 3, 2, NULL, 250000.00, 199000.00, 50, 0),
  (166, N'bbbasd-3-3', 29, 3, 3, NULL, 250000.00, 199000.00, 50, 0),
  (167, N'bbbasd-3-4', 29, 3, 4, NULL, 250000.00, 199000.00, 50, 0),
  (168, N'bbbasd-3-5', 29, 3, 5, NULL, 250000.00, 199000.00, 50, 0),
  (169, N'bbbasd-3-6', 29, 3, 6, NULL, 250000.00, 199000.00, 50, 0),
  (170, N'bbbasd-4-1', 29, 4, 1, NULL, 250000.00, 199000.00, 50, 0),
  (171, N'bbbasd-4-2', 29, 4, 2, NULL, 250000.00, 199000.00, 50, 0),
  (172, N'bbbasd-4-3', 29, 4, 3, NULL, 250000.00, 199000.00, 50, 0),
  (173, N'bbbasd-4-4', 29, 4, 4, NULL, 250000.00, 199000.00, 50, 0),
  (174, N'bbbasd-4-5', 29, 4, 5, NULL, 250000.00, 199000.00, 50, 0),
  (175, N'bbbasd-4-6', 29, 4, 6, NULL, 250000.00, 199000.00, 50, 0),
  (176, N'bbbasd-6-1', 29, 6, 1, NULL, 250000.00, 199000.00, 50, 0),
  (177, N'bbbasd-6-2', 29, 6, 2, NULL, 250000.00, 199000.00, 50, 0),
  (178, N'bbbasd-6-3', 29, 6, 3, NULL, 250000.00, 199000.00, 50, 0),
  (179, N'bbbasd-6-4', 29, 6, 4, NULL, 250000.00, 199000.00, 50, 0),
  (180, N'bbbasd-6-5', 29, 6, 5, NULL, 250000.00, 199000.00, 50, 0),
  (181, N'bbbasd-6-6', 29, 6, 6, NULL, 250000.00, 199000.00, 50, 0),
  (182, N'TGB-1-1', 30, 1, 1, NULL, 2500000.00, 1990000.00, 70, 1),
  (183, N'TGB-1-2', 30, 1, 2, NULL, 2500000.00, 1990000.00, 50, 1),
  (184, N'TGB-1-3', 30, 1, 3, NULL, 2500000.00, 1990000.00, 50, 1),
  (185, N'TGB-1-4', 30, 1, 4, NULL, 2500000.00, 1990000.00, 50, 1),
  (186, N'TGB-1-5', 30, 1, 5, NULL, 2500000.00, 1990000.00, 50, 1),
  (187, N'TGB-1-6', 30, 1, 6, NULL, 2500000.00, 1990000.00, 50, 1),
  (188, N'TGB-2-1', 30, 2, 1, NULL, 2500000.00, 1990000.00, 50, 1),
  (189, N'TGB-2-2', 30, 2, 2, NULL, 2500000.00, 1990000.00, 50, 1),
  (190, N'TGB-2-3', 30, 2, 3, NULL, 2500000.00, 1990000.00, 50, 1),
  (191, N'TGB-2-4', 30, 2, 4, NULL, 2500000.00, 1990000.00, 50, 1),
  (192, N'TGB-2-5', 30, 2, 5, NULL, 2500000.00, 1990000.00, 50, 1),
  (193, N'TGB-2-6', 30, 2, 6, NULL, 2500000.00, 1990000.00, 50, 1),
  (194, N'TGB-3-1', 30, 3, 1, NULL, 2500000.00, 1990000.00, 50, 1),
  (195, N'TGB-3-2', 30, 3, 2, NULL, 2500000.00, 1990000.00, 50, 1),
  (196, N'TGB-3-3', 30, 3, 3, NULL, 2500000.00, 1990000.00, 50, 1),
  (197, N'TGB-3-4', 30, 3, 4, NULL, 2500000.00, 1990000.00, 50, 1),
  (198, N'TGB-3-5', 30, 3, 5, NULL, 2500000.00, 1990000.00, 50, 1),
  (199, N'TGB-3-6', 30, 3, 6, NULL, 2500000.00, 1990000.00, 50, 1),
  (200, N'TGB-4-1', 30, 4, 1, NULL, 2500000.00, 1990000.00, 50, 1),
  (201, N'TGB-4-2', 30, 4, 2, NULL, 2500000.00, 1990000.00, 50, 1),
  (202, N'TGB-4-3', 30, 4, 3, NULL, 2500000.00, 1990000.00, 50, 1),
  (203, N'TGB-4-4', 30, 4, 4, NULL, 2500000.00, 1990000.00, 50, 1);
INSERT INTO [san_pham_chi_tiet] ([id], [ma_san_pham_chi_tiet], [id_san_pham], [id_mau_sac], [id_kich_thuoc], [id_khuyen_mai], [gia_ban_ban_dau], [gia_ban], [so_luong_ton], [trang_thai]) VALUES
  (204, N'TGB-4-5', 30, 4, 5, NULL, 2500000.00, 1990000.00, 50, 1),
  (205, N'TGB-4-6', 30, 4, 6, NULL, 2500000.00, 1990000.00, 50, 1),
  (206, N'TGB-6-1', 30, 6, 1, NULL, 2500000.00, 1990000.00, 50, 1),
  (207, N'TGB-6-2', 30, 6, 2, NULL, 2500000.00, 1990000.00, 50, 1),
  (208, N'TGB-6-3', 30, 6, 3, NULL, 2500000.00, 1990000.00, 50, 1),
  (209, N'TGB-6-4', 30, 6, 4, NULL, 2500000.00, 1990000.00, 50, 1),
  (210, N'TGB-6-5', 30, 6, 5, NULL, 2500000.00, 1990000.00, 50, 1),
  (211, N'TGB-6-6', 30, 6, 6, NULL, 2500000.00, 1990000.00, 50, 1);
SET IDENTITY_INSERT [san_pham_chi_tiet] OFF;
GO

-- -------------------------------------------------------------
-- Bang: giam_gia (2 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [giam_gia] ON;
GO
INSERT INTO [giam_gia] ([id], [ma_giam_gia], [ten_giam_gia], [loai_giam_gia], [gia_tri_giam], [toi_da_giam_gia], [gia_tri_toi_thieu], [so_luong], [ngay_bat_dau], [ngay_ket_thuc], [trang_thai], [mo_ta], [ngay_tao]) VALUES
  (1, N'VUITUOI10', N'Voucher Luôn Vui Tươi 10%', 1, 10.00, 50000.00, 200000.00, 100, '2026-01-01 00:00:00', '2026-12-31 00:00:00', 1, N'Giảm 10% tối đa 50k cho đơn từ 200k', '2026-10-11 12:49:37'),
  (2, N'GIAM30K', N'Giảm trực tiếp 30.000đ', 2, 30000.00, 30000.00, 300000.00, 50, '2026-01-01 00:00:00', '2026-12-31 00:00:00', 1, N'Giảm 30.000đ cho đơn từ 300k', '2026-10-11 12:49:37');
SET IDENTITY_INSERT [giam_gia] OFF;
GO

-- -------------------------------------------------------------
-- Bang: hoa_don (17 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [hoa_don] ON;
GO
INSERT INTO [hoa_don] ([id], [ma_hoa_don], [id_khach_hang], [id_nhan_vien], [id_giam_gia], [tong_tien], [tien_truoc_giam], [phi_giao_hang], [phuong_thuc_thanh_toan], [hinh_thuc_mua_hang], [dia_chi_giao_hang], [ghi_chu], [trang_thai], [ngay_tao]) VALUES
  (1, N'HD-20261011-001', 1, NULL, 1, 334470.00, 338300.00, 30000.00, 0, 0, N'Số 12 ngõ 45 Cầu Giấy, Hà Nội', N'Giao giờ hành chính', N'Đang giao', '2026-10-11 12:49:37'),
  (2, N'HD-20261011-002', 2, 2, NULL, 299000.00, 299000.00, 0.00, 0, 1, N'Mua tại quầy shop', N'Khách mua trực tiếp', N'Đã hoàn thành', '2026-10-11 12:49:37'),
  (3, N'HD-20261016-003', 1, 1, NULL, 537450.00, 507450.00, 30000.00, 1, 1, N'Số 10 Đường Cầu Giấy, Hà Nội', N'Giao giờ hành chính', N'Đã hoàn thành', '2026-10-16 15:46:01'),
  (4, N'HD-20261017-004', 2, 1, NULL, 498150.00, 468150.00, 30000.00, 0, 1, N'Số 15 Đường Cầu Giấy, Hà Nội', N'Gọi trước khi giao 15p', N'Đã hoàn thành', '2026-10-17 12:39:01'),
  (5, N'HD-20261017-005', 3, 1, NULL, 338300.00, 338300.00, 0.00, 1, 0, N'Mua trực tiếp tại Shop', N'Khách mua tại quầy', N'Đã hoàn thành', '2026-10-17 09:32:01'),
  (6, N'HD-20261018-006', 4, 1, NULL, 679000.00, 649000.00, 30000.00, 0, 1, N'Số 25 Đường Cầu Giấy, Hà Nội', N'Để bảo vệ nhận hộ', N'Đã hoàn thành', '2026-10-18 06:25:01'),
  (7, N'HD-20261019-007', 5, 1, NULL, 537450.00, 507450.00, 30000.00, 1, 1, N'Số 30 Đường Cầu Giấy, Hà Nội', N'Đóng gói cẩn thận giúp shop', N'Đã hoàn thành', '2026-10-19 15:18:01'),
  (8, N'HD-20261019-008', 1, 1, NULL, 767150.00, 767150.00, 0.00, 0, 0, N'Mua trực tiếp tại Shop', N'Khách ghé shop lấy', N'Đã hoàn thành', '2026-10-19 12:11:01'),
  (9, N'HD-20261020-009', 2, 1, NULL, 797150.00, 767150.00, 30000.00, 1, 1, N'Số 40 Đường Cầu Giấy, Hà Nội', N'Áo đẹp ủng hộ tiếp', N'Đã hoàn thành', '2026-10-20 09:04:01'),
  (10, N'HD-20261020-010', 3, 1, NULL, 537450.00, 507450.00, 30000.00, 0, 1, N'Số 45 Đường Cầu Giấy, Hà Nội', N'Ship gấp trong ngày', N'Đang giao', '2026-10-20 05:57:01'),
  (11, N'HD-20261021-011', 4, 1, NULL, 679000.00, 649000.00, 30000.00, 1, 1, N'Số 50 Đường Cầu Giấy, Hà Nội', N'Thanh toán VNPAY thành công', N'Đang giao', '2026-10-21 14:50:01'),
  (12, N'HD-20261021-012', 5, 1, NULL, 368300.00, 338300.00, 30000.00, 1, 1, N'Số 55 Đường Cầu Giấy, Hà Nội', N'Giao tầng 12 chung cư', N'Đã hoàn thành', '2026-10-21 11:43:01'),
  (13, N'HD-20261021-013', 1, 1, NULL, 498150.00, 468150.00, 30000.00, 0, 1, N'Số 60 Đường Cầu Giấy, Hà Nội', N'Chờ shop tư vấn size', N'Chờ xác nhận', '2026-10-21 08:36:01'),
  (14, N'HD-20261022-014', 2, 1, NULL, 537450.00, 507450.00, 30000.00, 1, 1, N'Số 65 Đường Cầu Giấy, Hà Nội', N'Đơn vừa đặt trưa nay', N'Chờ xác nhận', '2026-10-22 05:29:01'),
  (15, N'HD-20261022-015', 3, 1, NULL, 679000.00, 649000.00, 30000.00, 0, 1, N'Số 70 Đường Cầu Giấy, Hà Nội', N'Giao chiều nay', N'Chờ xác nhận', '2026-10-22 14:22:01'),
  (16, N'HD-20261018-016', 4, 1, NULL, 199150.00, 169150.00, 30000.00, 0, 1, N'Số 75 Đường Cầu Giấy, Hà Nội', N'Khách đổi ý muốn mẫu khác', N'Đã hủy', '2026-10-18 11:15:01'),
  (17, N'HD-20261019-017', 5, 1, NULL, 797150.00, 767150.00, 30000.00, 1, 1, N'Số 80 Đường Cầu Giấy, Hà Nội', N'Áo rất vừa vặn và thoáng', N'Đã hoàn thành', '2026-10-19 08:08:01');
SET IDENTITY_INSERT [hoa_don] OFF;
GO

-- -------------------------------------------------------------
-- Bang: hoa_don_chi_tiet (28 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [hoa_don_chi_tiet] ON;
GO
INSERT INTO [hoa_don_chi_tiet] ([id], [ma_hoa_don_chi_tiet], [id_hoa_don], [id_ao_dai_chi_tiet], [so_luong], [don_gia]) VALUES
  (1, N'HDCT01', 1, 1, 2, 169150.00),
  (2, N'HDCT02', 2, 5, 1, 299000.00),
  (3, N'HDCT-HD-20261016-003-01', 3, 1, 2, 169150.00),
  (4, N'HDCT-HD-20261016-003-02', 3, 2, 1, 169150.00),
  (5, N'HDCT-HD-20261017-004-01', 4, 3, 1, 169150.00),
  (6, N'HDCT-HD-20261017-004-02', 4, 5, 1, 299000.00),
  (7, N'HDCT-HD-20261017-005-01', 5, 4, 2, 169150.00),
  (8, N'HDCT-HD-20261018-006-01', 6, 6, 1, 299000.00),
  (9, N'HDCT-HD-20261018-006-02', 6, 7, 1, 350000.00),
  (10, N'HDCT-HD-20261019-007-01', 7, 2, 3, 169150.00),
  (11, N'HDCT-HD-20261019-008-01', 8, 5, 2, 299000.00),
  (12, N'HDCT-HD-20261019-008-02', 8, 3, 1, 169150.00),
  (13, N'HDCT-HD-20261020-009-01', 9, 1, 1, 169150.00),
  (14, N'HDCT-HD-20261020-009-02', 9, 6, 2, 299000.00),
  (15, N'HDCT-HD-20261020-010-01', 10, 4, 2, 169150.00),
  (16, N'HDCT-HD-20261020-010-02', 10, 2, 1, 169150.00),
  (17, N'HDCT-HD-20261021-011-01', 11, 7, 1, 350000.00),
  (18, N'HDCT-HD-20261021-011-02', 11, 5, 1, 299000.00),
  (19, N'HDCT-HD-20261021-012-01', 12, 3, 2, 169150.00),
  (20, N'HDCT-HD-20261021-013-01', 13, 2, 1, 169150.00),
  (21, N'HDCT-HD-20261021-013-02', 13, 6, 1, 299000.00),
  (22, N'HDCT-HD-20261022-014-01', 14, 1, 2, 169150.00),
  (23, N'HDCT-HD-20261022-014-02', 14, 4, 1, 169150.00),
  (24, N'HDCT-HD-20261022-015-01', 15, 5, 1, 299000.00),
  (25, N'HDCT-HD-20261022-015-02', 15, 7, 1, 350000.00),
  (26, N'HDCT-HD-20261018-016-01', 16, 3, 1, 169150.00),
  (27, N'HDCT-HD-20261019-017-01', 17, 6, 2, 299000.00),
  (28, N'HDCT-HD-20261019-017-02', 17, 2, 1, 169150.00);
SET IDENTITY_INSERT [hoa_don_chi_tiet] OFF;
GO

-- -------------------------------------------------------------
-- Bang: gio_hang_chi_tiet (2 dong)
-- -------------------------------------------------------------
SET IDENTITY_INSERT [gio_hang_chi_tiet] ON;
GO
INSERT INTO [gio_hang_chi_tiet] ([id], [id_khach_hang], [id_ao_dai_chi_tiet], [so_luong], [ngay_tao]) VALUES
  (1, 1, 1, 2, '2026-10-11 12:49:37'),
  (2, 1, 5, 1, '2026-10-11 12:49:37');
SET IDENTITY_INSERT [gio_hang_chi_tiet] OFF;
GO

