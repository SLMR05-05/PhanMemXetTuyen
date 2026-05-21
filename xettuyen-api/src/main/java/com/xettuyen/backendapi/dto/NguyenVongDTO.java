package com.xettuyen.backendapi.dto;

public class NguyenVongDTO {
    private Integer idNv;
    private String nvMaNganh;
    private String tenNganh;
    private Integer nvTt;
    private Double diemThxt;
    private Double diemUtqd;
    private Double diemCong;
    private Double diemXettuyen;
    private String nvKetqua;
    private String ttPhuongThuc;
    private String ttThm;

    public NguyenVongDTO() {
    }

    public NguyenVongDTO(Integer idNv, String nvMaNganh, String tenNganh, Integer nvTt,
            Double diemThxt, Double diemUtqd, Double diemCong, Double diemXettuyen,
            String nvKetqua, String ttPhuongThuc, String ttThm) {
        this.idNv = idNv;
        this.nvMaNganh = nvMaNganh;
        this.tenNganh = tenNganh;
        this.nvTt = nvTt;
        this.diemThxt = diemThxt;
        this.diemUtqd = diemUtqd;
        this.diemCong = diemCong;
        this.diemXettuyen = diemXettuyen;
        this.nvKetqua = nvKetqua;
        this.ttPhuongThuc = ttPhuongThuc;
        this.ttThm = ttThm;
    }

    public Integer getIdNv() {
        return idNv;
    }

    public void setIdNv(Integer idNv) {
        this.idNv = idNv;
    }

    public String getNvMaNganh() {
        return nvMaNganh;
    }

    public void setNvMaNganh(String nvMaNganh) {
        this.nvMaNganh = nvMaNganh;
    }

    public String getTenNganh() {
        return tenNganh;
    }

    public void setTenNganh(String tenNganh) {
        this.tenNganh = tenNganh;
    }

    public Integer getNvTt() {
        return nvTt;
    }

    public void setNvTt(Integer nvTt) {
        this.nvTt = nvTt;
    }

    public Double getDiemThxt() {
        return diemThxt;
    }

    public void setDiemThxt(Double diemThxt) {
        this.diemThxt = diemThxt;
    }

    public Double getDiemUtqd() {
        return diemUtqd;
    }

    public void setDiemUtqd(Double diemUtqd) {
        this.diemUtqd = diemUtqd;
    }

    public Double getDiemCong() {
        return diemCong;
    }

    public void setDiemCong(Double diemCong) {
        this.diemCong = diemCong;
    }

    public Double getDiemXettuyen() {
        return diemXettuyen;
    }

    public void setDiemXettuyen(Double diemXettuyen) {
        this.diemXettuyen = diemXettuyen;
    }

    public String getNvKetqua() {
        return nvKetqua;
    }

    public void setNvKetqua(String nvKetqua) {
        this.nvKetqua = nvKetqua;
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