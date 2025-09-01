package com.example.platform.dto;

import com.example.platform.model.Crypto;
import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestorDto {
    private Long id;
    private String name;
    private String surname;
    private String mernis;
    private String platformName;
    private String state;
    private String status;
    private String sicilNo;
    private List<PlatformDto> platform;
    private Set<CryptoDto> cryptos;



}
