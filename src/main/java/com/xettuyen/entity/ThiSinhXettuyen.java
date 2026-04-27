package com.xettuyen.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "xt_thisinhxettuyen25")
public class ThiSinhXettuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idthisinh")
    private Integer idThiSinh;

    @Column(name = "cccd", unique = true, length = 20)
    private String cccd;

    @Column(name = "sobaodanh")
    private String soBaoDanh;

    @Column(name = "ho")
    private String ho;

    @Column(name = "ten")
    private String ten;

    @Column(name = "ngay_sinh")
    private String ngaySinh;

    @Column(name = "dien_thoai")
    private String dienThoai;

    @Column(name = "password")
    private String password;

    @Column(name = "gioi_tinh")
    private String gioiTinh;

    @Column(name = "email")
    private String email;

    @Column(name = "noi_sinh")
    private String noiSinh;

    @Column(name = "updated_at")
    @Temporal(TemporalType.DATE)
    private Date updatedAt;

    @Column(name = "doi_tuong")
    private String doiTuong;

    @Column(name = "khu_vuc")
    private String khuVuc;

    // Constructors
    public ThiSinhXettuyen() {
    }

    public ThiSinhXettuyen(String cccd, String soBaoDanh, String ho, String ten, String ngaySinh,
                           String dienThoai, String password, String gioiTinh, String email,
                           String noiSinh, Date updatedAt, String doiTuong, String khuVuc) {
        this.cccd = cccd;
        this.soBaoDanh = soBaoDanh;
        this.ho = ho;
        this.ten = ten;
        this.ngaySinh = ngaySinh;
        this.dienThoai = dienThoai;
        this.password = password;
        this.gioiTinh = gioiTinh;
        this.email = email;
        this.noiSinh = noiSinh;
        this.updatedAt = updatedAt;
        this.doiTuong = doiTuong;
        this.khuVuc = khuVuc;
    }

    // Getters and Setters
    public Integer getIdThiSinh() {
        return idThiSinh;
    }

    public void setIdThiSinh(Integer idThiSinh) {
        this.idThiSinh = idThiSinh;
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

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoiSinh() {
        return noiSinh;
    }

    public void setNoiSinh(String noiSinh) {
        this.noiSinh = noiSinh;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDoiTuong() {
        return doiTuong;
    }

    public void setDoiTuong(String doiTuong) {
        this.doiTuong = doiTuong;
    }

    public String getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(String khuVuc) {
        this.khuVuc = khuVuc;
    }

    @Override
    public String toString() {
        return "ThiSinhXettuyen{" +
                "idThiSinh=" + idThiSinh +
                ", cccd='" + cccd + '\'' +
                ", soBaoDanh='" + soBaoDanh + '\'' +
                ", ho='" + ho + '\'' +
                ", ten='" + ten + '\'' +
                ", ngaySinh='" + ngaySinh + '\'' +
                ", dienThoai='" + dienThoai + '\'' +
                ", password='" + password + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", email='" + email + '\'' +
                ", noiSinh='" + noiSinh + '\'' +
                ", updatedAt=" + updatedAt +
                ", doiTuong='" + doiTuong + '\'' +
                ", khuVuc='" + khuVuc + '\'' +
                '}';
    }
}
