package com.example.platform.dto;

public class CryptoDTO {
    private String title;
    private Boolean status;

    public CryptoDTO(String title, Boolean status) {
        this.title = title;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getStatus() {
        return status;
    }
}
