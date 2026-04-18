package com.xettuyen.backendapi.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthLoginRequestDTO {

    @NotBlank(message = "cccd is required")
    private String cccd;

    @NotBlank(message = "password is required")
    private String password;

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}