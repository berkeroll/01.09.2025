package com.example.platform.Repository;

import com.example.platform.model.Investor;
import com.example.platform.model.Crypto;
import com.example.platform.model.InvestorWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorWalletRepository extends JpaRepository<InvestorWallet, Long> {
    Optional<InvestorWallet> findByInvestorAndCrypto(Investor investor, Crypto crypto);
}
