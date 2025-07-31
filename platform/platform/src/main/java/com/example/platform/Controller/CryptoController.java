package com.example.platform.Controller;

import com.example.platform.Service.CryptoService;
import com.example.platform.model.Crypto;
import com.example.platform.model.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/Crypto")
public class CryptoController {
    @Autowired
    CryptoService cryptoService;



    @PostMapping
    public Crypto addCrypto(@RequestBody Crypto crypto)
    {
        return cryptoService.addCrypto(crypto);

    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Crypto> updateInvestor(@PathVariable Long id, @RequestBody Crypto updatedCrypto) {
        Optional<Crypto> cryptoOptional = cryptoService.findById(id);

        if (cryptoOptional.isPresent()) {
            Crypto crypto = cryptoOptional.get();


            if (updatedCrypto.getCoins_title() != null) {
                crypto.setCoins_title(updatedCrypto.getCoins_title());
            }
            if (updatedCrypto.getCoins_code() != null) {
                crypto.setCoins_code(updatedCrypto.getCoins_code());
            }
            if (updatedCrypto.getCoins_tax() != null) {
                crypto.setCoins_tax(updatedCrypto.getCoins_tax());
            }
            if (updatedCrypto.getStatus()!=null)
            {
                crypto.setStatus(updatedCrypto.getStatus());
            }
            Crypto savedCrypto=cryptoService.addCrypto(crypto);
            return ResponseEntity.ok(savedCrypto);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Crypto> findAllCrypto()
    {
        return cryptoService.findAllCrypto();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrypto(@PathVariable Long id) {
        cryptoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }





}
