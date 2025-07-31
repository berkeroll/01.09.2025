package com.example.platform.dto;

import java.util.List;

public class PlatformResponseDTO {
    private String platformTitle;
    private List<CryptoDTO> cryptos;

    public PlatformResponseDTO(String platformTitle, List<CryptoDTO> cryptos) {
        this.platformTitle = platformTitle;
        this.cryptos = cryptos;
    }

    public String getPlatformTitle() {
        return platformTitle;
    }

    public List<CryptoDTO> getCryptos() {
        return cryptos;
    }
}
