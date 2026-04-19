package com.xettuyen.backendapi.dto;

public class NguyenVongDTO {

    private String maNganh;
    private String tenNganh;
    private Integer thuTuNguyenVong;

    public NguyenVongDTO() {
    }

    public NguyenVongDTO(String maNganh, String tenNganh, Integer thuTuNguyenVong) {
        this.maNganh = maNganh;
        this.tenNganh = tenNganh;
        this.thuTuNguyenVong = thuTuNguyenVong;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getTenNganh() {
        return tenNganh;
    }

    public void setTenNganh(String tenNganh) {
        this.tenNganh = tenNganh;
    }

    public Integer getThuTuNguyenVong() {
        return thuTuNguyenVong;
    }

    public void setThuTuNguyenVong(Integer thuTuNguyenVong) {
        this.thuTuNguyenVong = thuTuNguyenVong;
    }
}