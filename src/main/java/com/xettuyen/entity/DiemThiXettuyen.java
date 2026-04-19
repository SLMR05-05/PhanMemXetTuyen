package com.xettuyen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xt_diemthixettuyen")
public class DiemThiXettuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemthi")
    private Integer idDiemThi;

    @Column(name = "cccd", nullable = false, unique = true, length = 20)
    private String cccd;

    @Column(name = "sobaodanh")
    private String soBaoDanh;

    @Column(name = "d_phuongthuc")
    private String dPhuongThuc;

    @Column(name = "`TO`")
    private Double to;

    @Column(name = "LI")
    private Double li;

    @Column(name = "HO")
    private Double ho;

    @Column(name = "SI")
    private Double si;

    @Column(name = "SU")
    private Double su;

    @Column(name = "DI")
    private Double di;

    @Column(name = "`VA`")
    private Double va;

    @Column(name = "N1_THI", columnDefinition = "DECIMAL(8,2) COMMENT 'Điểm thi gốc'")
    private Double n1Thi;

    @Column(name = "N1_CC", columnDefinition = "DECIMAL(8,2) COMMENT 'max(N1_Thi, N1_QD)'")
    private Double n1Cc;

    @Column(name = "CNCN")
    private Double cncn;

    @Column(name = "CNNN")
    private Double cnnn;

    @Column(name = "TI")
    private Double ti;

    @Column(name = "KTPL")
    private Double ktpl;

    @Column(name = "NL1")
    private Double nl1;

    @Column(name = "NK1")
    private Double nk1;

    @Column(name = "NK2")
    private Double nk2;

    // Constructors
    public DiemThiXettuyen() {
    }

    public DiemThiXettuyen(String cccd, String soBaoDanh, String dPhuongThuc, Double to, Double li,
                           Double ho, Double si, Double su, Double di, Double va, Double n1Thi,
                           Double n1Cc, Double cncn, Double cnnn, Double ti, Double ktpl,
                           Double nl1, Double nk1, Double nk2) {
        this.cccd = cccd;
        this.soBaoDanh = soBaoDanh;
        this.dPhuongThuc = dPhuongThuc;
        this.to = to;
        this.li = li;
        this.ho = ho;
        this.si = si;
        this.su = su;
        this.di = di;
        this.va = va;
        this.n1Thi = n1Thi;
        this.n1Cc = n1Cc;
        this.cncn = cncn;
        this.cnnn = cnnn;
        this.ti = ti;
        this.ktpl = ktpl;
        this.nl1 = nl1;
        this.nk1 = nk1;
        this.nk2 = nk2;
    }

    // Getters and Setters
    public Integer getIdDiemThi() {
        return idDiemThi;
    }

    public void setIdDiemThi(Integer idDiemThi) {
        this.idDiemThi = idDiemThi;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getSoBaoDanh() {
        return soBaoDanh;
    }

    public void setSoBaoDanh(String soBaoDanh) {
        this.soBaoDanh = soBaoDanh;
    }

    public String getDPhuongThuc() {
        return dPhuongThuc;
    }

    public void setDPhuongThuc(String dPhuongThuc) {
        this.dPhuongThuc = dPhuongThuc;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public Double getLi() {
        return li;
    }

    public void setLi(Double li) {
        this.li = li;
    }

    public Double getHo() {
        return ho;
    }

    public void setHo(Double ho) {
        this.ho = ho;
    }

    public Double getSi() {
        return si;
    }

    public void setSi(Double si) {
        this.si = si;
    }

    public Double getSu() {
        return su;
    }

    public void setSu(Double su) {
        this.su = su;
    }

    public Double getDi() {
        return di;
    }

    public void setDi(Double di) {
        this.di = di;
    }

    public Double getVa() {
        return va;
    }

    public void setVa(Double va) {
        this.va = va;
    }

    public Double getN1Thi() {
        return n1Thi;
    }

    public void setN1Thi(Double n1Thi) {
        this.n1Thi = n1Thi;
    }

    public Double getN1Cc() {
        return n1Cc;
    }

    public void setN1Cc(Double n1Cc) {
        this.n1Cc = n1Cc;
    }

    public Double getCncn() {
        return cncn;
    }

    public void setCncn(Double cncn) {
        this.cncn = cncn;
    }

    public Double getCnnn() {
        return cnnn;
    }

    public void setCnnn(Double cnnn) {
        this.cnnn = cnnn;
    }

    public Double getTi() {
        return ti;
    }

    public void setTi(Double ti) {
        this.ti = ti;
    }

    public Double getKtpl() {
        return ktpl;
    }

    public void setKtpl(Double ktpl) {
        this.ktpl = ktpl;
    }

    public Double getNl1() {
        return nl1;
    }

    public void setNl1(Double nl1) {
        this.nl1 = nl1;
    }

    public Double getNk1() {
        return nk1;
    }

    public void setNk1(Double nk1) {
        this.nk1 = nk1;
    }

    public Double getNk2() {
        return nk2;
    }

    public void setNk2(Double nk2) {
        this.nk2 = nk2;
    }

    @Override
    public String toString() {
        return "DiemThiXettuyen{" +
                "idDiemThi=" + idDiemThi +
                ", cccd='" + cccd + '\'' +
                ", soBaoDanh='" + soBaoDanh + '\'' +
                ", dPhuongThuc='" + dPhuongThuc + '\'' +
                ", to=" + to +
                ", li=" + li +
                ", ho=" + ho +
                ", si=" + si +
                ", su=" + su +
                ", di=" + di +
                ", va=" + va +
                ", n1Thi=" + n1Thi +
                ", n1Cc=" + n1Cc +
                ", cncn=" + cncn +
                ", cnnn=" + cnnn +
                ", ti=" + ti +
                ", ktpl=" + ktpl +
                ", nl1=" + nl1 +
                ", nk1=" + nk1 +
                ", nk2=" + nk2 +
                '}';
    }
}
