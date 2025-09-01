package com.example.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatformDto {
    private Long id;
    private String platformTitle;
    private String platformCode;
    private String taxNo;
    private Boolean state;
    private List<InvestorDto> investors;
    private Set<CryptoDto>cryptos;
    private String recordStatus;
    private UUID apiKey;

}
