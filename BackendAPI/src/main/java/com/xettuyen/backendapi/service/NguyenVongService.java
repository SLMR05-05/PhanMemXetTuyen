package com.xettuyen.backendapi.service;

import com.xettuyen.backendapi.dto.NguyenVongDTO;
import com.xettuyen.backendapi.dto.NguyenVongRequestDTO;
import com.xettuyen.backendapi.entity.Nganh;
import com.xettuyen.backendapi.entity.NguyenVongXettuyen;
import com.xettuyen.backendapi.entity.ThiSinhXettuyen;
import com.xettuyen.backendapi.repository.NganhRepository;
import com.xettuyen.backendapi.repository.NguyenVongRepository;
import com.xettuyen.backendapi.repository.ThiSinhRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Transactional
    public NguyenVongDTO createNguyenVong(Authentication authentication, NguyenVongRequestDTO requestDTO) {
        String cccd = getCurrentCccd(authentication);

        ThiSinhXettuyen thiSinh = thiSinhRepository.findByCccd(cccd)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thi sinh not found"));

        Nganh nganh = nganhRepository.findByMaNganh(requestDTO.getMaNganh())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nganh not found"));

        Integer thuTu = requestDTO.getThuTuNguyenVong();

        if (nguyenVongRepository.existsByNnCccdAndNvMaNganh(cccd, requestDTO.getMaNganh())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nguyen vong for this nganh already exists");
        }

        if (nguyenVongRepository.existsByNnCccdAndNvTt(cccd, thuTu)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nguyen vong order already exists");
        }

        NguyenVongXettuyen entity = new NguyenVongXettuyen();
        entity.setNnCccd(thiSinh.getCccd());
        entity.setNvMaNganh(requestDTO.getMaNganh());
        entity.setNvTt(thuTu);
        entity.setTtPhuongThuc(requestDTO.getTtPhuongThuc());
        entity.setTtThm(requestDTO.getTtThm());
        entity.setNvKetqua("Chờ xét");
        entity.setNvKeys(buildNvKey(cccd, requestDTO.getMaNganh(), thuTu));

        NguyenVongXettuyen saved = nguyenVongRepository.save(entity);
        return toDto(saved, nganh);
    }

    private String getCurrentCccd(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated CCCD");
        }
        return authentication.getName();
    }

    private String buildNvKey(String cccd, String maNganh, Integer thuTu) {
        return cccd + "_" + maNganh + "_" + thuTu;
    }

    private NguyenVongDTO toDto(NguyenVongXettuyen entity) {
        Nganh nganh = nganhRepository.findByMaNganh(entity.getNvMaNganh()).orElse(null);
        return toDto(entity, nganh);
    }

    private NguyenVongDTO toDto(NguyenVongXettuyen entity, Nganh nganh) {
        String tenNganh = nganh == null ? null : nganh.getTenNganh();
        return new NguyenVongDTO(entity.getNvMaNganh(), tenNganh, entity.getNvTt());
    }
}