package com.xettuyen.backendapi.service;

import com.xettuyen.backendapi.dto.AuthLoginRequestDTO;
import com.xettuyen.backendapi.entity.ThiSinhXettuyen;
import com.xettuyen.backendapi.repository.ThiSinhRepository;
import com.xettuyen.backendapi.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final ThiSinhRepository thiSinhRepository;
    private final JwtUtil jwtUtil;

    public AuthService(ThiSinhRepository thiSinhRepository, JwtUtil jwtUtil) {
        this.thiSinhRepository = thiSinhRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(AuthLoginRequestDTO requestDTO) {
        String cccd = requestDTO.getCccd() == null ? null : requestDTO.getCccd().trim();
        String password = requestDTO.getPassword();

        ThiSinhXettuyen thiSinh = thiSinhRepository.findByCccdAndPassword(cccd, password)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "CCCD or password is invalid"));

        if (thiSinh.getCccd() == null || thiSinh.getCccd().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thi sinh has no CCCD data");
        }

        return jwtUtil.generateToken(thiSinh.getCccd());
    }
}