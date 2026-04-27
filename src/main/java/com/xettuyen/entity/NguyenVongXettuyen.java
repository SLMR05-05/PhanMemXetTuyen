package com.xettuyen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
public class NguyenVongXettuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnv")
    private Integer idNv;

    @Column(name = "nn_cccd", nullable = false)
    private String nnCccd;

    @Column(name = "nv_manganh", nullable = false)
    private String nvMaNganh;

    @Column(name = "nv_tt", nullable = false)
    private Integer nvTt;

    @Column(name = "diem_thxt", columnDefinition = "DECIMAL(10,5) COMMENT 'đã cộng điểm môn chính'")
    private Double diemThxt;

    @Column(name = "diem_utqd", columnDefinition = "DECIMAL(10,5) COMMENT 'Điểm UTQD theo tổ họp sẽ khác nhau.'")
    private Double diemUtqd;

    @Column(name = "diem_cong", columnDefinition = "DECIMAL(6,2) COMMENT 'Tong 3 mon chua tinh mon chinh'")
    private Double diemCong;

    @Column(name = "diem_xettuyen", columnDefinition = "DECIMAL(10,5) COMMENT 'đã cộng điểm ưu tiên'")
    private Double diemXettuyen;

    @Column(name = "nv_ketqua")
    private String nvKetqua;

    @Column(name = "nv_keys", unique = true)
    private String nvKeys;

    @Column(name = "tt_phuongthuc")
    private String ttPhuongThuc;

    @Column(name = "tt_thm")
    private String ttThm;

    // Constructors
    public NguyenVongXettuyen() {
    }

    public NguyenVongXettuyen(String nnCccd, String nvMaNganh, Integer nvTt, Double diemThxt,
                              Double diemUtqd, Double diemCong, Double diemXettuyen, String nvKetqua,
                              String nvKeys, String ttPhuongThuc, String ttThm) {
        this.nnCccd = nnCccd;
        this.nvMaNganh = nvMaNganh;
        this.nvTt = nvTt;
        this.diemThxt = diemThxt;
        this.diemUtqd = diemUtqd;
        this.diemCong = diemCong;
        this.diemXettuyen = diemXettuyen;
        this.nvKetqua = nvKetqua;
        this.nvKeys = nvKeys;
        this.ttPhuongThuc = ttPhuongThuc;
        this.ttThm = ttThm;
    }

    // Getters and Setters
    public Integer getIdNv() {
        return idNv;
    }

    public void setIdNv(Integer idNv) {
        this.idNv = idNv;
    }

    public String getNnCccd() {
        return nnCccd;
    }

    public void setNnCccd(String nnCccd) {
        this.nnCccd = nnCccd;
    }

    public String getNvMaNganh() {
        return nvMaNganh;
    }

    public void setNvMaNganh(String nvMaNganh) {
        this.nvMaNganh = nvMaNganh;
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

    public String getNvKeys() {
        return nvKeys;
    }

    public void setNvKeys(String nvKeys) {
        this.nvKeys = nvKeys;
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

    @Override
    public String toString() {
        return "NguyenVongXettuyen{" +
                "idNv=" + idNv +
                ", nnCccd='" + nnCccd + '\'' +
                ", nvMaNganh='" + nvMaNganh + '\'' +
                ", nvTt=" + nvTt +
                ", diemThxt=" + diemThxt +
                ", diemUtqd=" + diemUtqd +
                ", diemCong=" + diemCong +
                ", diemXettuyen=" + diemXettuyen +
                ", nvKetqua='" + nvKetqua + '\'' +
                ", nvKeys='" + nvKeys + '\'' +
                ", ttPhuongThuc='" + ttPhuongThuc + '\'' +
                ", ttThm='" + ttThm + '\'' +
                '}';
    }
}
