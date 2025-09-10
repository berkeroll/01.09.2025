package com.example.platform.Service;
import com.example.platform.Repository.*;
import com.example.platform.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final InvestorRepository investorRepository;
    private final PlatformRepository platformRepository;
    private final CryptoRepository cryptoRepository;
    private final InvestorWalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction executeTransaction(Long platformId,
                                          String buyerSicilNo,
                                          String sellerSicilNo,
                                          String baseCryptoCode,
                                          String quoteCryptoCode,
                                          BigDecimal amount,
                                          BigDecimal price) {

        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new IllegalArgumentException("Platform bulunamadı"));

        Investor buyer = investorRepository.findBySicilNoAndPlatformId(buyerSicilNo,platformId)
                .orElseThrow(() -> new IllegalArgumentException("Alıcı bulunamadı"));

        if (!platform.getInvestors().contains(buyer)) {
            throw new IllegalArgumentException("Alıcı bu platforma kayıtlı değil");
        }

        Investor seller = investorRepository.findBySicilNoAndPlatformId(sellerSicilNo,platformId)
                .orElseThrow(() -> new IllegalArgumentException("Satıcı bulunamadı"));

        if (!platform.getInvestors().contains(seller)) {
            throw new IllegalArgumentException("Satıcı bu platforma kayıtlı değil");
        }

        // Crypto code ile bul
        Crypto base = cryptoRepository.findByCoinsCode(baseCryptoCode)
                .orElseThrow(() -> new IllegalArgumentException("Base crypto bulunamadı"));

        Crypto quote = cryptoRepository.findByCoinsCode(quoteCryptoCode)
                .orElseThrow(() -> new IllegalArgumentException("Quote crypto bulunamadı"));

        if (!platform.getCryptos().contains(base)) {
            throw new IllegalArgumentException("Base crypto bu platformda işlem görmüyor");
        }

        if (!platform.getCryptos().contains(quote)) {
            throw new IllegalArgumentException("Quote crypto bu platformda işlem görmüyor");
        }

        Transaction tx = Transaction.builder()
                .platform(platform)
                .buyerRegNo(buyerSicilNo)
                .sellerRegNo(sellerSicilNo)
                .baseCurrency(base)
                .quoteCurrency(quote)
                .amount(amount)
                .price(price)
                .build();

        transactionRepository.save(tx);

        // Wallet güncelleme
        updateInvestorWallet(buyer, base, amount);            // Buyer base alır
        updateInvestorWallet(seller, base, amount.negate());  // Seller base verir

        updateInvestorWallet(buyer, quote, price.negate());   // Buyer quote öder
        updateInvestorWallet(seller, quote, price);           // Seller quote alır

        return tx;
    }


    @Transactional
    public void updateInvestorWallet(Investor investor, Crypto crypto, BigDecimal amount) {
        InvestorWallet wallet = walletRepository.findByInvestorAndCrypto(investor, crypto)
                .orElseGet(() -> {
                    InvestorWallet w = new InvestorWallet();
                    w.setInvestor(investor);
                    w.setCrypto(crypto);
                    w.setBalance(BigDecimal.ZERO);
                    return w;
                });

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }
}
