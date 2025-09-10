package com.example.platform.Controller;

import com.example.platform.dto.TransactionRequestDto;
import com.example.platform.model.Transaction;
import com.example.platform.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionRequestDto> executeTransaction(@RequestBody TransactionRequestDto request) {

        // Service tarafında transaction oluşturuluyor
        Transaction transaction = transactionService.executeTransaction(
                request.getPlatformId(),
                request.getBuyerSicilNo(),
                request.getSellerSicilNo(),
                request.getBaseCryptoCode(),
                request.getQuoteCryptoCode(),
                request.getAmount(),
                request.getPrice()
        );

        // Entity'den DTO'ya dönüşüm
        TransactionRequestDto responseDto = new TransactionRequestDto();
        responseDto.setPlatformId(transaction.getId());
        responseDto.setPlatformId(transaction.getPlatform().getId());
        responseDto.setBuyerSicilNo(transaction.getBuyerRegNo());
        responseDto.setSellerSicilNo(transaction.getSellerRegNo());
        responseDto.setBaseCryptoCode(transaction.getBaseCurrency().getCoinsCode());
        responseDto.setQuoteCryptoCode(transaction.getQuoteCurrency().getCoinsCode());
        responseDto.setAmount(transaction.getAmount());
        responseDto.setPrice(transaction.getPrice());

        return ResponseEntity.ok(responseDto);
    }
}

