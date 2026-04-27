package com.xettuyen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xt_bangquydoi")
public class BangQuydoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idqd")
    private Integer idQd;

    @Column(name = "d_phuongthuc")
    private String dPhuongThuc;

    @Column(name = "d_tohop")
    private String dTohop;

    @Column(name = "d_mon")
    private String dMon;

    @Column(name = "d_diema")
    private Double dDiemA;

    @Column(name = "d_diemb")
    private Double dDiemB;

    @Column(name = "d_diemc")
    private Double dDiemC;

    @Column(name = "d_diemd")
    private Double dDiemD;

    @Column(name = "d_maquydoi", unique = true)
    private String dMaQuydoi;

    @Column(name = "d_phanvi")
    private String dPhanvi;

    // Constructors
    public BangQuydoi() {
    }

    public BangQuydoi(String dPhuongThuc, String dTohop, String dMon, Double dDiemA,
                      Double dDiemB, Double dDiemC, Double dDiemD, String dMaQuydoi, String dPhanvi) {
        this.dPhuongThuc = dPhuongThuc;
        this.dTohop = dTohop;
        this.dMon = dMon;
        this.dDiemA = dDiemA;
        this.dDiemB = dDiemB;
        this.dDiemC = dDiemC;
        this.dDiemD = dDiemD;
        this.dMaQuydoi = dMaQuydoi;
        this.dPhanvi = dPhanvi;
    }

    // Getters and Setters
    public Integer getIdQd() {
        return idQd;
    }

    public void setIdQd(Integer idQd) {
        this.idQd = idQd;
    }

    public String getDPhuongThuc() {
        return dPhuongThuc;
    }

    public void setDPhuongThuc(String dPhuongThuc) {
        this.dPhuongThuc = dPhuongThuc;
    }

    public String getDTohop() {
        return dTohop;
    }

    public void setDTohop(String dTohop) {
        this.dTohop = dTohop;
    }

    public String getDMon() {
        return dMon;
    }

    public void setDMon(String dMon) {
        this.dMon = dMon;
    }

    public Double getDDiemA() {
        return dDiemA;
    }

    public void setDDiemA(Double dDiemA) {
        this.dDiemA = dDiemA;
    }

    public Double getDDiemB() {
        return dDiemB;
    }

    public void setDDiemB(Double dDiemB) {
        this.dDiemB = dDiemB;
    }

    public Double getDDiemC() {
        return dDiemC;
    }

    public void setDDiemC(Double dDiemC) {
        this.dDiemC = dDiemC;
    }

    public Double getDDiemD() {
        return dDiemD;
    }

    public void setDDiemD(Double dDiemD) {
        this.dDiemD = dDiemD;
    }

    public String getDMaQuydoi() {
        return dMaQuydoi;
    }

    public void setDMaQuydoi(String dMaQuydoi) {
        this.dMaQuydoi = dMaQuydoi;
    }

    public String getDPhanvi() {
        return dPhanvi;
    }

    public void setDPhanvi(String dPhanvi) {
        this.dPhanvi = dPhanvi;
    }

    @Override
    public String toString() {
        return "BangQuydoi{" +
                "idQd=" + idQd +
                ", dPhuongThuc='" + dPhuongThuc + '\'' +
                ", dTohop='" + dTohop + '\'' +
                ", dMon='" + dMon + '\'' +
                ", dDiemA=" + dDiemA +
                ", dDiemB=" + dDiemB +
                ", dDiemC=" + dDiemC +
                ", dDiemD=" + dDiemD +
                ", dMaQuydoi='" + dMaQuydoi + '\'' +
                ", dPhanvi='" + dPhanvi + '\'' +
                '}';
    }
}
