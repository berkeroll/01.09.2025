package com.example.platform.Service;

import com.example.platform.Repository.CryptoRepository;
import com.example.platform.model.Crypto;
import com.example.platform.model.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CryptoService {
    @Autowired
    CryptoRepository cryptoRepository;

    public Crypto addCrypto(Crypto crypto)
    {

                var result=cryptoRepository.save(crypto);
        return result;

    }
    public Optional<Crypto> findById(Long id) {
        return cryptoRepository.findById(id);
    }

    public List<Crypto> findAllCrypto()
    {
        return cryptoRepository.findAll();

    }

    public void deleteById(Long id) {
        cryptoRepository.deleteById(id);
    }
}




