package com.xettuyen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xt_tohop_monthi")
public class TohopMonthi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtohop")
    private Integer idTohop;

    @Column(name = "matohop", nullable = false, unique = true)
    private String maTohop;

    @Column(name = "mon1", nullable = false)
    private String mon1;

    @Column(name = "mon2", nullable = false)
    private String mon2;

    @Column(name = "mon3", nullable = false)
    private String mon3;

    @Column(name = "tentohop")
    private String tenTohop;

    // Constructors
    public TohopMonthi() {
    }

    public TohopMonthi(String maTohop, String mon1, String mon2, String mon3, String tenTohop) {
        this.maTohop = maTohop;
        this.mon1 = mon1;
        this.mon2 = mon2;
        this.mon3 = mon3;
        this.tenTohop = tenTohop;
    }

    // Getters and Setters
    public Integer getIdTohop() {
        return idTohop;
    }

    public void setIdTohop(Integer idTohop) {
        this.idTohop = idTohop;
    }

    public String getMaTohop() {
        return maTohop;
    }

    public void setMaTohop(String maTohop) {
        this.maTohop = maTohop;
    }

    public String getMon1() {
        return mon1;
    }

    public void setMon1(String mon1) {
        this.mon1 = mon1;
    }

    public String getMon2() {
        return mon2;
    }

    public void setMon2(String mon2) {
        this.mon2 = mon2;
    }

    public String getMon3() {
        return mon3;
    }

    public void setMon3(String mon3) {
        this.mon3 = mon3;
    }

    public String getTenTohop() {
        return tenTohop;
    }

    public void setTenTohop(String tenTohop) {
        this.tenTohop = tenTohop;
    }

    @Override
    public String toString() {
        return "TohopMonthi{" +
                "idTohop=" + idTohop +
                ", maTohop='" + maTohop + '\'' +
                ", mon1='" + mon1 + '\'' +
                ", mon2='" + mon2 + '\'' +
                ", mon3='" + mon3 + '\'' +
                ", tenTohop='" + tenTohop + '\'' +
                '}';
    }
}
