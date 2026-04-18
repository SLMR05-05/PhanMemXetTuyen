package com.xettuyen.backendapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "xt_nganh")
public class Nganh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnganh")
    private Integer idNganh;

    @Column(name = "manganh", nullable = false)
    private String maNganh;

    @Column(name = "tennganh", nullable = false)
    private String tenNganh;

    @Column(name = "n_chitieu")
    private Integer nChiTieu;

    @Column(name = "n_diemsan")
    private Double nDiemSan;

    public Integer getIdNganh() {
        return idNganh;
    }

    public void setIdNganh(Integer idNganh) {
        this.idNganh = idNganh;
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

    public Integer getnChiTieu() {
        return nChiTieu;
    }

    public void setnChiTieu(Integer nChiTieu) {
        this.nChiTieu = nChiTieu;
    }

    public Double getnDiemSan() {
        return nDiemSan;
    }

    public void setnDiemSan(Double nDiemSan) {
        this.nDiemSan = nDiemSan;
    }
}