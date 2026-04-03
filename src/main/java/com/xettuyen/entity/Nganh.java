package com.xettuyen.entity;

import jakarta.persistence.*;

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

    @Column(name = "n_tohopgoc")
    private String nTohopGoc;

    @Column(name = "n_chitieu")
    private Integer nChiTieu;

    @Column(name = "n_diemsan")
    private Double nDiemSan;

    @Column(name = "n_diemtrungtuyen")
    private Double nDiemTrungTuyen;

    @Column(name = "n_tuyenthang")
    private String nTuyenThang;

    @Column(name = "n_dgnl")
    private String nDgNl;

    @Column(name = "n_thpt")
    private String nThpt;

    @Column(name = "n_vsat")
    private String nVsat;

    @Column(name = "sl_xtt")
    private Integer slXtt;

    @Column(name = "sl_dgnl")
    private Integer slDgNl;

    @Column(name = "sl_vsat")
    private Integer slVsat;

    @Column(name = "sl_thpt")
    private String slThpt;

    // Constructors
    public Nganh() {
    }

    public Nganh(String maNganh, String tenNganh, String nTohopGoc, Integer nChiTieu,
                 Double nDiemSan, Double nDiemTrungTuyen, String nTuyenThang, String nDgNl,
                 String nThpt, String nVsat, Integer slXtt, Integer slDgNl, Integer slVsat, String slThpt) {
        this.maNganh = maNganh;
        this.tenNganh = tenNganh;
        this.nTohopGoc = nTohopGoc;
        this.nChiTieu = nChiTieu;
        this.nDiemSan = nDiemSan;
        this.nDiemTrungTuyen = nDiemTrungTuyen;
        this.nTuyenThang = nTuyenThang;
        this.nDgNl = nDgNl;
        this.nThpt = nThpt;
        this.nVsat = nVsat;
        this.slXtt = slXtt;
        this.slDgNl = slDgNl;
        this.slVsat = slVsat;
        this.slThpt = slThpt;
    }

    // Getters and Setters
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

    public String getNTohopGoc() {
        return nTohopGoc;
    }

    public void setNTohopGoc(String nTohopGoc) {
        this.nTohopGoc = nTohopGoc;
    }

    public Integer getNChiTieu() {
        return nChiTieu;
    }

    public void setNChiTieu(Integer nChiTieu) {
        this.nChiTieu = nChiTieu;
    }

    public Double getNDiemSan() {
        return nDiemSan;
    }

    public void setNDiemSan(Double nDiemSan) {
        this.nDiemSan = nDiemSan;
    }

    public Double getNDiemTrungTuyen() {
        return nDiemTrungTuyen;
    }

    public void setNDiemTrungTuyen(Double nDiemTrungTuyen) {
        this.nDiemTrungTuyen = nDiemTrungTuyen;
    }

    public String getNTuyenThang() {
        return nTuyenThang;
    }

    public void setNTuyenThang(String nTuyenThang) {
        this.nTuyenThang = nTuyenThang;
    }

    public String getNDgNl() {
        return nDgNl;
    }

    public void setNDgNl(String nDgNl) {
        this.nDgNl = nDgNl;
    }

    public String getNThpt() {
        return nThpt;
    }

    public void setNThpt(String nThpt) {
        this.nThpt = nThpt;
    }

    public String getNVsat() {
        return nVsat;
    }

    public void setNVsat(String nVsat) {
        this.nVsat = nVsat;
    }

    public Integer getSlXtt() {
        return slXtt;
    }

    public void setSlXtt(Integer slXtt) {
        this.slXtt = slXtt;
    }

    public Integer getSlDgNl() {
        return slDgNl;
    }

    public void setSlDgNl(Integer slDgNl) {
        this.slDgNl = slDgNl;
    }

    public Integer getSlVsat() {
        return slVsat;
    }

    public void setSlVsat(Integer slVsat) {
        this.slVsat = slVsat;
    }

    public String getSlThpt() {
        return slThpt;
    }

    public void setSlThpt(String slThpt) {
        this.slThpt = slThpt;
    }

    @Override
    public String toString() {
        return "Nganh{" +
                "idNganh=" + idNganh +
                ", maNganh='" + maNganh + '\'' +
                ", tenNganh='" + tenNganh + '\'' +
                ", nTohopGoc='" + nTohopGoc + '\'' +
                ", nChiTieu=" + nChiTieu +
                ", nDiemSan=" + nDiemSan +
                ", nDiemTrungTuyen=" + nDiemTrungTuyen +
                ", nTuyenThang='" + nTuyenThang + '\'' +
                ", nDgNl='" + nDgNl + '\'' +
                ", nThpt='" + nThpt + '\'' +
                ", nVsat='" + nVsat + '\'' +
                ", slXtt=" + slXtt +
                ", slDgNl=" + slDgNl +
                ", slVsat=" + slVsat +
                ", slThpt='" + slThpt + '\'' +
                '}';
    }
}
