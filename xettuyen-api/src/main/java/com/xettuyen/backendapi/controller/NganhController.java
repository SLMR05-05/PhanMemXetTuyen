package com.xettuyen.backendapi.controller;

import com.xettuyen.backendapi.dto.NganhDTO;
import com.xettuyen.backendapi.service.NganhService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nganh")
public class NganhController {

    private final NganhService nganhService;

    public NganhController(NganhService nganhService) {
        this.nganhService = nganhService;
    }

    @GetMapping
    public List<NganhDTO> getAllNganh() {
        return nganhService.getAllNganh();
    }
}