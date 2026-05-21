package com.xettuyen.backendapi.controller;

import com.xettuyen.backendapi.dto.NguyenVongDTO;
import com.xettuyen.backendapi.dto.NguyenVongRequestDTO;
import com.xettuyen.backendapi.service.NguyenVongService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/nguyenvong")
public class NguyenVongController {

    private final NguyenVongService nguyenVongService;

    public NguyenVongController(NguyenVongService nguyenVongService) {
        this.nguyenVongService = nguyenVongService;
    }

    @GetMapping
    public List<NguyenVongDTO> getMyNguyenVong(Authentication authentication) {
        return nguyenVongService.getCurrentStudentNguyenVong(authentication);
    }

    @GetMapping("/lookup")
    public List<NguyenVongDTO> getNguyenVongByCccd(@RequestParam String cccd) {
        return nguyenVongService.getNguyenVongByCccd(cccd);
    }

    @PostMapping
    public NguyenVongDTO createNguyenVong(Authentication authentication,
            @Valid @RequestBody NguyenVongRequestDTO requestDTO) {
        return nguyenVongService.createNguyenVong(authentication, requestDTO);
    }

    @PutMapping("/swap/{id1}/{id2}")
    public List<NguyenVongDTO> swapNguyenVong(Authentication authentication, 
                                              @PathVariable Integer id1, 
                                              @PathVariable Integer id2) {
        return nguyenVongService.swapNguyenVong(authentication, id1, id2);
    }

    @DeleteMapping("/{idNv}")
    public void deleteNguyenVong(Authentication authentication, @PathVariable Integer idNv) {
        nguyenVongService.deleteNguyenVong(authentication, idNv);
    }
}