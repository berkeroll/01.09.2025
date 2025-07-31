package com.example.platform.Repository;


import com.example.platform.model.Crypto;
import com.example.platform.model.Investor;
import com.example.platform.model.PlatformCrypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto,Long> {



}
