package com.xettuyen.backendapi.dto;

public class NganhDTO {

    private String maNganh;
    private String tenNganh;

    public NganhDTO() {
    }

    public NganhDTO(String maNganh, String tenNganh) {
        this.maNganh = maNganh;
        this.tenNganh = tenNganh;
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
}