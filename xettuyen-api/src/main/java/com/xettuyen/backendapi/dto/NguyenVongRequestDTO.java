package com.xettuyen.backendapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NguyenVongRequestDTO {

    @NotBlank(message = "maNganh is required")
    private String maNganh;

    @NotNull(message = "thuTuNguyenVong is required")
    @Min(value = 1, message = "thuTuNguyenVong must be greater than 0")
    private Integer thuTuNguyenVong;

    private String ttPhuongThuc;

    private String ttThm;

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public Integer getThuTuNguyenVong() {
        return thuTuNguyenVong;
    }

    public void setThuTuNguyenVong(Integer thuTuNguyenVong) {
        this.thuTuNguyenVong = thuTuNguyenVong;
    }

    public String getTtPhuongThuc() {
        return ttPhuongThuc;
    }

    public void setTtPhuongThuc(String ttPhuongThuc) {
        this.ttPhuongThuc = ttPhuongThuc;
    }

    public String getTtThm() {
        return ttThm;
    }

    public void setTtThm(String ttThm) {
        this.ttThm = ttThm;
    }
}