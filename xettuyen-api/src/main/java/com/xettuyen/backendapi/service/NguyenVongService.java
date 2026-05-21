package com.xettuyen.backendapi.service;

import com.xettuyen.backendapi.dto.NguyenVongDTO;
import com.xettuyen.backendapi.dto.NguyenVongRequestDTO;
import com.xettuyen.backendapi.repository.NganhRepository;
import com.xettuyen.backendapi.repository.NguyenVongRepository;
import com.xettuyen.backendapi.repository.ThiSinhRepository;
import com.xettuyen.entity.Nganh;
import com.xettuyen.entity.NguyenVongXettuyen;
import com.xettuyen.entity.ThiSinhXettuyen;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@Service
public class NguyenVongService {

    private final NguyenVongRepository nguyenVongRepository;
    private final NganhRepository nganhRepository;
    private final ThiSinhRepository thiSinhRepository;

    public NguyenVongService(NguyenVongRepository nguyenVongRepository,
            NganhRepository nganhRepository,
            ThiSinhRepository thiSinhRepository) {
        this.nguyenVongRepository = nguyenVongRepository;
        this.nganhRepository = nganhRepository;
        this.thiSinhRepository = thiSinhRepository;
    }

    public List<NguyenVongDTO> getCurrentStudentNguyenVong(Authentication authentication) {
        String cccd = getCurrentCccd(authentication);
        return nguyenVongRepository.findByNnCccdOrderByNvTtAsc(cccd)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<NguyenVongDTO> getNguyenVongByCccd(String cccd) {
        if (cccd == null || cccd.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CCCD is required");
        }

        thiSinhRepository.findByCccd(cccd)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thi sinh not found"));

        return nguyenVongRepository.findByNnCccdOrderByNvTtAsc(cccd)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public NguyenVongDTO createNguyenVong(Authentication authentication, NguyenVongRequestDTO requestDTO) {
        String cccd = getCurrentCccd(authentication);
        
        // 1. Kiểm tra xem sinh viên đã đăng ký nguyện vọng cho ngành này chưa
        if (nguyenVongRepository.existsByNnCccdAndNvMaNganh(cccd, requestDTO.getMaNganh())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đã tồn tại nguyện vọng cho ngành này.");
        }

        // 2. Tự động tính toán thứ tự kế tiếp trong danh sách
        List<NguyenVongXettuyen> danhSachHienTai = nguyenVongRepository.findByNnCccdOrderByNvTtAsc(cccd);
        int thuTuKeTiep = 1;
        if (danhSachHienTai != null && !danhSachHienTai.isEmpty()) {
            // Lấy thứ tự của nguyện vọng cuối cùng trong danh sách và cộng thêm 1
            thuTuKeTiep = danhSachHienTai.get(danhSachHienTai.size() - 1).getNvTt() + 1;
        }

        // 3. Khởi tạo và lưu Nguyện vọng mới
        NguyenVongXettuyen entity = new NguyenVongXettuyen();
        entity.setNnCccd(cccd);
        entity.setNvMaNganh(requestDTO.getMaNganh());
        entity.setNvTt(thuTuKeTiep); // Ghi đè thứ tự tự động tính
        
        // Cập nhật các thông tin khác...
        
        Nganh nganh = nganhRepository.findByMaNganh(requestDTO.getMaNganh()).orElse(null);
        if (nganh != null) {
            entity.setNvKetqua("Chờ xét");
        }
        
        entity.setNvKeys(buildNvKey(cccd, requestDTO.getMaNganh()));

        NguyenVongXettuyen saved = nguyenVongRepository.save(entity);
        return toDto(saved, nganh);
    }

    private String getCurrentCccd(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated CCCD");
        }
        return authentication.getName();
    }

    private String buildNvKey(String cccd, String maNganh) {
        return cccd + "_" + maNganh;
    }

    private NguyenVongDTO toDto(NguyenVongXettuyen entity) {
        Nganh nganh = nganhRepository.findByMaNganh(entity.getNvMaNganh()).orElse(null);
        return toDto(entity, nganh);
    }

    private NguyenVongDTO toDto(NguyenVongXettuyen entity, Nganh nganh) {
        String tenNganh = nganh == null ? null : nganh.getTenNganh();
        return new NguyenVongDTO(
                entity.getIdNv(),
                entity.getNvMaNganh(),
                tenNganh,
                entity.getNvTt(),
                entity.getDiemThxt(),
                entity.getDiemUtqd(),
                entity.getDiemCong(),
                entity.getDiemXettuyen(),
                entity.getNvKetqua(),
                entity.getTtPhuongThuc(),
                entity.getTtThm());
    }

    @Transactional
    public List<NguyenVongDTO> swapNguyenVong(Authentication authentication, Integer id1, Integer id2) {
        String cccd = getCurrentCccd(authentication);

        // Lấy 2 nguyện vọng từ DB
        NguyenVongXettuyen nv1 = nguyenVongRepository.findById(id1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nguyện vọng 1"));
        NguyenVongXettuyen nv2 = nguyenVongRepository.findById(id2)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nguyện vọng 2"));

        // Xác thực bảo mật: Đảm bảo cả 2 nguyện vọng này đều thuộc về user đang đăng nhập
        if (!nv1.getNnCccd().equals(cccd) || !nv2.getNnCccd().equals(cccd)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền chỉnh sửa nguyện vọng này.");
        }

        // Đổi chỗ thuộc tính nvTt (Thứ tự nguyện vọng)
        Integer tempTt = nv1.getNvTt();
        nv1.setNvTt(nv2.getNvTt());
        nv2.setNvTt(tempTt);

        // Lưu thay đổi
        nguyenVongRepository.save(nv1);
        nguyenVongRepository.save(nv2);

        // Trả về danh sách mới đã được sắp xếp lại để Frontend cập nhật UI
        return getCurrentStudentNguyenVong(authentication);
    }

    @Transactional
    public void deleteNguyenVong(Authentication authentication, Integer idNv) {
        String cccd = getCurrentCccd(authentication);

        // 1. Tìm nguyện vọng cần xóa
        NguyenVongXettuyen nvToDelete = nguyenVongRepository.findById(idNv)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nguyện vọng cần xóa"));

        // 2. Xác thực quyền (Chỉ cho phép sinh viên xóa nguyện vọng của chính mình)
        if (!nvToDelete.getNnCccd().equals(cccd)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa nguyện vọng này");
        }

        // 3. Thực hiện xóa
        nguyenVongRepository.delete(nvToDelete);

        // 4. Lấy danh sách các nguyện vọng còn lại và đánh lại số thứ tự (nvTt)
        List<NguyenVongXettuyen> remainingList = nguyenVongRepository.findByNnCccdOrderByNvTtAsc(cccd);
        int currentTt = 1;
        for (NguyenVongXettuyen nv : remainingList) {
            if (nv.getNvTt() != currentTt) {
                nv.setNvTt(currentTt);
                nguyenVongRepository.save(nv); // Cập nhật lại số thứ tự mới
            }
            currentTt++;
        }
    }
}