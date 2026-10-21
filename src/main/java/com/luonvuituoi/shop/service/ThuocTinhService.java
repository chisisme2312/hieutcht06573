package com.luonvuituoi.shop.service;

import com.luonvuituoi.shop.entity.*;
import java.util.List;
import java.util.Optional;

public interface ThuocTinhService {
    // Mau Sac
    List<MauSac> getAllMauSac();
    Optional<MauSac> getMauSacById(Integer id);
    MauSac saveMauSac(MauSac mauSac);
    void deleteMauSac(Integer id);

    // Kich Thuoc
    List<KichThuoc> getAllKichThuoc();
    Optional<KichThuoc> getKichThuocById(Integer id);
    KichThuoc saveKichThuoc(KichThuoc kichThuoc);
    void deleteKichThuoc(Integer id);

    // Chat Lieu
    List<ChatLieu> getAllChatLieu();
    Optional<ChatLieu> getChatLieuById(Integer id);
    ChatLieu saveChatLieu(ChatLieu chatLieu);
    void deleteChatLieu(Integer id);

    // Co Ao
    List<CoAo> getAllCoAo();
    Optional<CoAo> getCoAoById(Integer id);
    CoAo saveCoAo(CoAo coAo);
    void deleteCoAo(Integer id);

    // Loai San Pham
    List<LoaiSanPham> getAllLoaiSanPham();
    Optional<LoaiSanPham> getLoaiSanPhamById(Integer id);
    LoaiSanPham saveLoaiSanPham(LoaiSanPham loaiSanPham);
    void deleteLoaiSanPham(Integer id);

    // Nha Cung Cap
    List<NhaCungCap> getAllNhaCungCap();
    Optional<NhaCungCap> getNhaCungCapById(Integer id);
    NhaCungCap saveNhaCungCap(NhaCungCap nhaCungCap);
    void deleteNhaCungCap(Integer id);
}
