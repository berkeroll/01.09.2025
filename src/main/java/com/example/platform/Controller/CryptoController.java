package com.example.platform.Controller;

import com.example.platform.Repository.CryptoRepository;
import com.example.platform.Repository.PlatformRepository;
import com.example.platform.Service.CryptoService;
import com.example.platform.dto.CryptoDto;
import com.example.platform.model.Crypto;
import com.example.platform.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {
    @Autowired
    CryptoService cryptoService;
    @Autowired
    CryptoRepository cryptoRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    PlatformRepository platformRepository;


    @PostMapping
    public CryptoDto addCrypto(@RequestBody CryptoDto cryptoDto) {

        Crypto crypto=new Crypto();
        crypto.setCoinsTitle(cryptoDto.getCoinsTitle());
        crypto.setCoinsCode(cryptoDto.getCoinsCode());
        crypto.setCoinsTax(cryptoDto.getCoinsTax());
        crypto.setStatus(cryptoDto.getStatus());

        Crypto saveCrypto= cryptoService.addCrypto(crypto);

        CryptoDto saveCryptoDto=cryptoService.convertToCryptoDto(saveCrypto);

        return saveCryptoDto;

    }


    @PutMapping("/update/{id}")
    public ResponseEntity<CryptoDto> updateCrypto(@PathVariable Long id, @RequestBody CryptoDto updatedCrypto, @RequestHeader("Authorization") String token) {

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token alınamadı");
        }

        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        } else {
            token = token.trim();
        }

        if (jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("Token geçersiz veya süresi dolmuş");
        }


        Optional<Crypto> cryptoOptional = cryptoService.findById(id);

        if (cryptoOptional.isPresent()) {
            Crypto crypto = cryptoOptional.get();


            if (updatedCrypto.getCoinsTitle() != null) {
                crypto.setCoinsTitle(updatedCrypto.getCoinsTitle());
            }
            if (updatedCrypto.getCoinsCode() != null) {
                crypto.setCoinsCode(updatedCrypto.getCoinsCode());
            }
            if (updatedCrypto.getCoinsTax() != null) {
                crypto.setCoinsTax(updatedCrypto.getCoinsTax());
            }
            if (updatedCrypto.getStatus() != null) {
                crypto.setStatus(updatedCrypto.getStatus());
            }
            Crypto savedCrypto = cryptoService.updatedSaveCrypto(crypto);
            CryptoDto savedCryptoDto=cryptoService.convertToCryptoDto(savedCrypto);
            return ResponseEntity.ok(savedCryptoDto);
        }
        return ResponseEntity.notFound().build();
    }

//    @GetMapping
//    public List<Crypto> findAllCrypto() {
//        return cryptoService.findAllCrypto();
//    }

    @GetMapping
    public List<CryptoDto> findAllCrypto() {
        List<Crypto> cryptos = cryptoService.findAllCrypto();

        List<CryptoDto> cryptoDtos = cryptos.stream()
                .map(cryptoService::convertToCryptoDto) // Service içindeki dönüşüm metodunu kullanıyoruz
                .collect(Collectors.toList());

        return cryptoDtos;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteCrypto(@PathVariable Long id) {
        cryptoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
