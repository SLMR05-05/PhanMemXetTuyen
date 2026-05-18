package com.xettuyen.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tohop")
public class ToHop {

    @Id
    @Column(name = "ma_to_hop")
    private String maToHop;

    @Column(name = "ten_to_hop")
    private String tenToHop;

    // Getter - Setter
    public String getMaToHop() { return maToHop; }
    public void setMaToHop(String maToHop) { this.maToHop = maToHop; }

    public String getTenToHop() { return tenToHop; }
    public void setTenToHop(String tenToHop) { this.tenToHop = tenToHop; }
}