package com.example.platform.dto;

import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoDto {

private Long id;
private String coinsTitle;
private String coinsCode;
private String coinsTax;
private Boolean status;
private Set<PlatformDto> platforms;
private Set<InvestorDto> investors;


}
