package com.example.platform.Repository;


import com.example.platform.model.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto,Long> {

    boolean existsByCoinsTitle(String coinsTitle);
    boolean existsByCoinsCode(String coinsCode);
    boolean existsByCoinsTax(String coinsTax);
    Optional<Crypto> findByCoinsCode(String coinsCode);



}
