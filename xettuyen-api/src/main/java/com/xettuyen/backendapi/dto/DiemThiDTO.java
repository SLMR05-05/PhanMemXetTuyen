package com.xettuyen.backendapi.dto;

public class DiemThiDTO {
    private String monThi;
    private Double diem;
    private String kyThi;

    public DiemThiDTO(String monThi, Double diem, String kyThi) {
        this.monThi = monThi;
        this.diem = diem;
        this.kyThi = kyThi;
    }

    public String getMonThi() { return monThi; }
    public void setMonThi(String monThi) { this.monThi = monThi; }

    public Double getDiem() { return diem; }
    public void setDiem(Double diem) { this.diem = diem; }

    public String getKyThi() { return kyThi; }
    public void setKyThi(String kyThi) { this.kyThi = kyThi; }
}