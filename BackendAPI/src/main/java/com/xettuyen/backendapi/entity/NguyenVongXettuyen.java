package com.xettuyen.backendapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "diem_thxt")
    private Double diemThxt;

    @Column(name = "diem_utqd")
    private Double diemUtqd;

    @Column(name = "diem_cong")
    private Double diemCong;

    @Column(name = "diem_xettuyen")
    private Double diemXettuyen;

    @Column(name = "nv_ketqua")
    private String nvKetqua;

    @Column(name = "nv_keys", unique = true)
    private String nvKeys;

    @Column(name = "tt_phuongthuc")
    private String ttPhuongThuc;

    @Column(name = "tt_thm")
    private String ttThm;

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
}