package com.example.platform.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequestDto {
    private Long platformId;
    private String buyerSicilNo;
    private String sellerSicilNo;
    private String baseCryptoCode;
    private String quoteCryptoCode;
    private BigDecimal amount;
    private BigDecimal price;
}
