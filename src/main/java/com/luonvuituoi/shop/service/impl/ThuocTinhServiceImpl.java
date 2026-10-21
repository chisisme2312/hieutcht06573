package com.luonvuituoi.shop.service.impl;

import com.luonvuituoi.shop.entity.*;
import com.luonvuituoi.shop.repository.*;
import com.luonvuituoi.shop.service.ThuocTinhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThuocTinhServiceImpl implements ThuocTinhService {

    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final CoAoRepository coAoRepository;
    private final LoaiSanPhamRepository loaiSanPhamRepository;
    private final NhaCungCapRepository nhaCungCapRepository;

    // Mau Sac
    @Override
    public List<MauSac> getAllMauSac() {
        return mauSacRepository.findAll();
    }

    @Override
    public Optional<MauSac> getMauSacById(Integer id) {
        return mauSacRepository.findById(id);
    }

    @Override
    public MauSac saveMauSac(MauSac mauSac) {
        if (mauSac.getId() == null && mauSac.getNgayTao() == null) {
            mauSac.setNgayTao(LocalDateTime.now());
        }
        if (mauSac.getTrangThai() == null) {
            mauSac.setTrangThai(true);
        }
        return mauSacRepository.save(mauSac);
    }

    @Override
    public void deleteMauSac(Integer id) {
        mauSacRepository.deleteById(id);
    }

    // Kich Thuoc
    @Override
    public List<KichThuoc> getAllKichThuoc() {
        return kichThuocRepository.findAll();
    }

    @Override
    public Optional<KichThuoc> getKichThuocById(Integer id) {
        return kichThuocRepository.findById(id);
    }

    @Override
    public KichThuoc saveKichThuoc(KichThuoc kichThuoc) {
        if (kichThuoc.getId() == null && kichThuoc.getNgayTao() == null) {
            kichThuoc.setNgayTao(LocalDateTime.now());
        }
        if (kichThuoc.getTrangThai() == null) {
            kichThuoc.setTrangThai(true);
        }
        return kichThuocRepository.save(kichThuoc);
    }

    @Override
    public void deleteKichThuoc(Integer id) {
        kichThuocRepository.deleteById(id);
    }

    // Chat Lieu
    @Override
    public List<ChatLieu> getAllChatLieu() {
        return chatLieuRepository.findAll();
    }

    @Override
    public Optional<ChatLieu> getChatLieuById(Integer id) {
        return chatLieuRepository.findById(id);
    }

    @Override
    public ChatLieu saveChatLieu(ChatLieu chatLieu) {
        if (chatLieu.getId() == null && chatLieu.getNgayTao() == null) {
            chatLieu.setNgayTao(LocalDateTime.now());
        }
        if (chatLieu.getTrangThai() == null) {
            chatLieu.setTrangThai(true);
        }
        return chatLieuRepository.save(chatLieu);
    }

    @Override
    public void deleteChatLieu(Integer id) {
        chatLieuRepository.deleteById(id);
    }

    // Co Ao
    @Override
    public List<CoAo> getAllCoAo() {
        return coAoRepository.findAll();
    }

    @Override
    public Optional<CoAo> getCoAoById(Integer id) {
        return coAoRepository.findById(id);
    }

    @Override
    public CoAo saveCoAo(CoAo coAo) {
        if (coAo.getId() == null && coAo.getNgayTao() == null) {
            coAo.setNgayTao(LocalDateTime.now());
        }
        if (coAo.getTrangThai() == null) {
            coAo.setTrangThai(true);
        }
        return coAoRepository.save(coAo);
    }

    @Override
    public void deleteCoAo(Integer id) {
        coAoRepository.deleteById(id);
    }

    // Loai San Pham
    @Override
    public List<LoaiSanPham> getAllLoaiSanPham() {
        return loaiSanPhamRepository.findAll();
    }

    @Override
    public Optional<LoaiSanPham> getLoaiSanPhamById(Integer id) {
        return loaiSanPhamRepository.findById(id);
    }

    @Override
    public LoaiSanPham saveLoaiSanPham(LoaiSanPham loaiSanPham) {
        if (loaiSanPham.getId() == null && loaiSanPham.getNgayTao() == null) {
            loaiSanPham.setNgayTao(LocalDateTime.now());
        }
        if (loaiSanPham.getTrangThai() == null) {
            loaiSanPham.setTrangThai(true);
        }
        return loaiSanPhamRepository.save(loaiSanPham);
    }

    @Override
    public void deleteLoaiSanPham(Integer id) {
        loaiSanPhamRepository.deleteById(id);
    }

    // Nha Cung Cap
    @Override
    public List<NhaCungCap> getAllNhaCungCap() {
        return nhaCungCapRepository.findAll();
    }

    @Override
    public Optional<NhaCungCap> getNhaCungCapById(Integer id) {
        return nhaCungCapRepository.findById(id);
    }

    @Override
    public NhaCungCap saveNhaCungCap(NhaCungCap nhaCungCap) {
        if (nhaCungCap.getId() == null && nhaCungCap.getNgayTao() == null) {
            nhaCungCap.setNgayTao(LocalDateTime.now());
        }
        if (nhaCungCap.getTrangThai() == null) {
            nhaCungCap.setTrangThai(true);
        }
        return nhaCungCapRepository.save(nhaCungCap);
    }

    @Override
    public void deleteNhaCungCap(Integer id) {
        nhaCungCapRepository.deleteById(id);
    }
}
