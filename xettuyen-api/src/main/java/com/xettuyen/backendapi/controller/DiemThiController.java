package com.xettuyen.backendapi.controller;

import com.xettuyen.backendapi.dto.DiemThiDTO;
import com.xettuyen.backendapi.service.DiemThiService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/diemthi")
public class DiemThiController {

    private final DiemThiService diemThiService;

    public DiemThiController(DiemThiService diemThiService) {
        this.diemThiService = diemThiService;
    }

    @GetMapping
    public List<DiemThiDTO> getMyDiemThi(Authentication authentication) {
        return diemThiService.getMyDiemThi(authentication);
    }
}