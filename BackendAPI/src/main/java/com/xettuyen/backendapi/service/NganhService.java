package com.xettuyen.backendapi.service;

import com.xettuyen.backendapi.dto.NganhDTO;
import com.xettuyen.backendapi.entity.Nganh;
import com.xettuyen.backendapi.repository.NganhRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NganhService {

    private final NganhRepository nganhRepository;

    public NganhService(NganhRepository nganhRepository) {
        this.nganhRepository = nganhRepository;
    }

    public List<NganhDTO> getAllNganh() {
        return nganhRepository.findAllByOrderByMaNganhAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public NganhDTO toDto(Nganh nganh) {
        return new NganhDTO(nganh.getMaNganh(), nganh.getTenNganh());
    }
}