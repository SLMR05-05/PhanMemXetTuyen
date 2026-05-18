package com.xettuyen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xt_diemcongxettuyen")
public class DiemCongXettuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemcong")
    private Integer idDiemCong;

    @Column(name = "ts_cccd", nullable = false)
    private String tsCccd;

    @Column(name = "manganh")
    private String maNganh;

    @Column(name = "matohop")
    private String maTohop;

    @Column(name = "phuongthuc")
    private String phuongThuc;

    @Column(name = "diemCC")
    private Double diemCc;

    @Column(name = "diemUtxt")
    private Double diemUtxt;

    @Column(name = "diemTong")
    private Double diemTong;

    @Column(name = "ghichu", columnDefinition = "text")
    private String ghiChu;

    @Column(name = "dc_keys", nullable = false, unique = true)
    private String dcKeys;

    // Constructors
    public DiemCongXettuyen() {
    }

    public DiemCongXettuyen(String tsCccd, String maNganh, String maTohop, String phuongThuc,
                            Double diemCc, Double diemUtxt, Double diemTong, String ghiChu, String dcKeys) {
        this.tsCccd = tsCccd;
        this.maNganh = maNganh;
        this.maTohop = maTohop;
        this.phuongThuc = phuongThuc;
        this.diemCc = diemCc;
        this.diemUtxt = diemUtxt;
        this.diemTong = diemTong;
        this.ghiChu = ghiChu;
        this.dcKeys = dcKeys;
    }

    // Getters and Setters
    public Integer getIdDiemCong() {
        return idDiemCong;
    }

    public void setIdDiemCong(Integer idDiemCong) {
        this.idDiemCong = idDiemCong;
    }

    public String getTsCccd() {
        return tsCccd;
    }

    public void setTsCccd(String tsCccd) {
        this.tsCccd = tsCccd;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getMaTohop() {
        return maTohop;
    }

    public void setMaTohop(String maTohop) {
        this.maTohop = maTohop;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public Double getDiemCc() {
        return diemCc;
    }

    public void setDiemCc(Double diemCc) {
        this.diemCc = diemCc;
    }

    public Double getDiemUtxt() {
        return diemUtxt;
    }

    public void setDiemUtxt(Double diemUtxt) {
        this.diemUtxt = diemUtxt;
    }

    public Double getDiemTong() {
        return diemTong;
    }

    public void setDiemTong(Double diemTong) {
        this.diemTong = diemTong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getDcKeys() {
        return dcKeys;
    }

    public void setDcKeys(String dcKeys) {
        this.dcKeys = dcKeys;
    }

    @Override
    public String toString() {
        return "DiemCongXettuyen{" +
                "idDiemCong=" + idDiemCong +
                ", tsCccd='" + tsCccd + '\'' +
                ", maNganh='" + maNganh + '\'' +
                ", maTohop='" + maTohop + '\'' +
                ", phuongThuc='" + phuongThuc + '\'' +
                ", diemCc=" + diemCc +
                ", diemUtxt=" + diemUtxt +
                ", diemTong=" + diemTong +
                ", ghiChu='" + ghiChu + '\'' +
                ", dcKeys='" + dcKeys + '\'' +
                '}';
    }
}
