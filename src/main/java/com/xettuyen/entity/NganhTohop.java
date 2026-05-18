package com.xettuyen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xt_nganh_tohop")
public class NganhTohop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "manganh", nullable = false)
    private String maNganh;

    @Column(name = "matohop", nullable = false)
    private String maTohop;

    @Column(name = "th_mon1")
    private String thMon1;

    @Column(name = "hsmon1")
    private Integer hsMon1;

    @Column(name = "th_mon2")
    private String thMon2;

    @Column(name = "hsmon2")
    private Integer hsMon2;

    @Column(name = "th_mon3")
    private String thMon3;

    @Column(name = "hsmon3")
    private Integer hsMon3;

    @Column(name = "tb_keys", unique = true, columnDefinition = "VARCHAR(45) COMMENT 'manganh_matohop'")
    private String tbKeys;

    @Column(name = "N1")
    private Integer n1;

    @Column(name = "`TO`")
    private Integer to;

    @Column(name = "LI")
    private Integer li;

    @Column(name = "HO")
    private Integer ho;

    @Column(name = "SI")
    private Integer si;

    @Column(name = "VA")
    private Integer va;

    @Column(name = "SU")
    private Integer su;

    @Column(name = "DI")
    private Integer di;

    @Column(name = "TI")
    private Integer ti;

    @Column(name = "KHAC")
    private Integer khac;

    @Column(name = "KTPL")
    private Integer ktpl;

    @Column(name = "dolech")
    private Double doLech;

    // Constructors
    public NganhTohop() {
    }

    public NganhTohop(String maNganh, String maTohop, String thMon1, Integer hsMon1,
                      String thMon2, Integer hsMon2, String thMon3, Integer hsMon3,
                      String tbKeys, Integer n1, Integer to, Integer li, Integer ho,
                      Integer si, Integer va, Integer su, Integer di, Integer ti,
                      Integer khac, Integer ktpl, Double doLech) {
        this.maNganh = maNganh;
        this.maTohop = maTohop;
        this.thMon1 = thMon1;
        this.hsMon1 = hsMon1;
        this.thMon2 = thMon2;
        this.hsMon2 = hsMon2;
        this.thMon3 = thMon3;
        this.hsMon3 = hsMon3;
        this.tbKeys = tbKeys;
        this.n1 = n1;
        this.to = to;
        this.li = li;
        this.ho = ho;
        this.si = si;
        this.va = va;
        this.su = su;
        this.di = di;
        this.ti = ti;
        this.khac = khac;
        this.ktpl = ktpl;
        this.doLech = doLech;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getThMon1() {
        return thMon1;
    }

    public void setThMon1(String thMon1) {
        this.thMon1 = thMon1;
    }

    public Integer getHsMon1() {
        return hsMon1;
    }

    public void setHsMon1(Integer hsMon1) {
        this.hsMon1 = hsMon1;
    }

    public String getThMon2() {
        return thMon2;
    }

    public void setThMon2(String thMon2) {
        this.thMon2 = thMon2;
    }

    public Integer getHsMon2() {
        return hsMon2;
    }

    public void setHsMon2(Integer hsMon2) {
        this.hsMon2 = hsMon2;
    }

    public String getThMon3() {
        return thMon3;
    }

    public void setThMon3(String thMon3) {
        this.thMon3 = thMon3;
    }

    public Integer getHsMon3() {
        return hsMon3;
    }

    public void setHsMon3(Integer hsMon3) {
        this.hsMon3 = hsMon3;
    }

    public String getTbKeys() {
        return tbKeys;
    }

    public void setTbKeys(String tbKeys) {
        this.tbKeys = tbKeys;
    }

    public Integer getN1() {
        return n1;
    }

    public void setN1(Integer n1) {
        this.n1 = n1;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getLi() {
        return li;
    }

    public void setLi(Integer li) {
        this.li = li;
    }

    public Integer getHo() {
        return ho;
    }

    public void setHo(Integer ho) {
        this.ho = ho;
    }

    public Integer getSi() {
        return si;
    }

    public void setSi(Integer si) {
        this.si = si;
    }

    public Integer getVa() {
        return va;
    }

    public void setVa(Integer va) {
        this.va = va;
    }

    public Integer getSu() {
        return su;
    }

    public void setSu(Integer su) {
        this.su = su;
    }

    public Integer getDi() {
        return di;
    }

    public void setDi(Integer di) {
        this.di = di;
    }

    public Integer getTi() {
        return ti;
    }

    public void setTi(Integer ti) {
        this.ti = ti;
    }

    public Integer getKhac() {
        return khac;
    }

    public void setKhac(Integer khac) {
        this.khac = khac;
    }

    public Integer getKtpl() {
        return ktpl;
    }

    public void setKtpl(Integer ktpl) {
        this.ktpl = ktpl;
    }

    public Double getDoLech() {
        return doLech;
    }

    public void setDoLech(Double doLech) {
        this.doLech = doLech;
    }

    @Override
    public String toString() {
        return "NganhTohop{" +
                "id=" + id +
                ", maNganh='" + maNganh + '\'' +
                ", maTohop='" + maTohop + '\'' +
                ", thMon1='" + thMon1 + '\'' +
                ", hsMon1=" + hsMon1 +
                ", thMon2='" + thMon2 + '\'' +
                ", hsMon2=" + hsMon2 +
                ", thMon3='" + thMon3 + '\'' +
                ", hsMon3=" + hsMon3 +
                ", tbKeys='" + tbKeys + '\'' +
                ", n1=" + n1 +
                ", to=" + to +
                ", li=" + li +
                ", ho=" + ho +
                ", si=" + si +
                ", va=" + va +
                ", su=" + su +
                ", di=" + di +
                ", ti=" + ti +
                ", khac=" + khac +
                ", ktpl=" + ktpl +
                ", doLech=" + doLech +
                '}';
    }
}
