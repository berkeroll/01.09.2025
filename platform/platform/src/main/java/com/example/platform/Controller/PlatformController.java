package com.example.platform.Controller;

import com.example.platform.Repository.CryptoRepository;
import com.example.platform.Service.PlatformService;
import com.example.platform.model.Crypto;
import com.example.platform.model.Platform;
import com.example.platform.model.PlatformCrypto;
import com.example.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
//import java.util.UUID;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {

    private final PlatformService platformService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    CryptoRepository cryptoRepository;








    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;

    }

    @GetMapping
    public List<Platform> getAllPlatforms() {
        return platformService.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Platform> getPlatformById(@PathVariable Long id) {
        var result= platformService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        return result;
    }





    @GetMapping("/coins/{id}")
    public ResponseEntity<?> getPlatformWithCryptos(@PathVariable Long id) {
        return platformService.getPlatformWithCryptos(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/v1/{id}")
    public ResponseEntity<?> apiKey(@PathVariable Long id) {
        Optional<Platform> optionalPlatform = platformService.findById(id);

        if (optionalPlatform.isPresent()) {
            Platform platform = optionalPlatform.get();


            if (platform.getApiKey() != null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Bu platformun zaten bir API Key'i var.");
            }


            platform.setApiKey(UUID.randomUUID());

            Platform updatedPlatform = platformService.save(platform);
            return ResponseEntity.ok(updatedPlatform);

        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Platform bulunamadı: ID = " + id);
        }
    }

    @PutMapping("/v1/{id}/refresh")
    public ResponseEntity<?> refreshApiKey(@PathVariable Long id) {
        Optional<Platform> optionalPlatform = platformService.findById(id);

        if (optionalPlatform.isPresent()) {
            Platform platform = optionalPlatform.get();


            platform.setApiKey(UUID.randomUUID());

            Platform updatedPlatform = platformService.save(platform);
            return ResponseEntity.ok("Yeni API Key oluşturuldu: " + updatedPlatform.getApiKey());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Platform bulunamadı: ID = " + id);
    }


    @PostMapping
    public Platform createPlatform(@RequestBody Platform platform) {

        platform.setApiKey(UUID.randomUUID());
        return platformService.save(platform);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String apiKeyStr = body.get("apiKey");

        if (apiKeyStr == null || apiKeyStr.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("API Key zorunludur.");
        }

        try {
            UUID apiKey = UUID.fromString(apiKeyStr);

            Optional<Platform> platformOpt = platformService.findApi(apiKey);


            if (platformOpt.isPresent()) {
                Platform platform=platformOpt.get();
                Date issuedAt=new Date();
                Date expiresAt = new Date(issuedAt.getTime() + JwtUtil.EXPIRATION_TIME);
                String jwt = jwtUtil.generateTokenWithApiKey(apiKey,platform,issuedAt,expiresAt);
                Map<String,String> response=new HashMap<>();
                response.put("token",jwt);
                response.put("platformTitle",platform.getPlatformTitle());
                response.put("platformCode",platform.getPlatformCode());
                response.put("issueadAt",issuedAt.toString());
                response.put("expiressAt",expiresAt.toString());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Geçersiz API Key");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body("API Key formatı geçersiz (UUID bekleniyor)");
        }
    }

    @GetMapping("/secure")
    public ResponseEntity<String> secureTest() {
        return ResponseEntity.ok("Token geçerli, korumalı alan");
    }


@PutMapping("update/{id}")

public ResponseEntity<Platform> updateInvestor(@PathVariable Long id, @RequestBody Platform updatedPlatform) {
    Optional<Platform> platformOptional = platformService.findById(id);

    if (platformOptional.isPresent()) {
        Platform platform = platformOptional.get();


        if (updatedPlatform.getPlatformTitle() != null) {
            platform.setPlatformTitle(updatedPlatform.getPlatformTitle());
        }
        if (updatedPlatform.getPlatformCode() != null) {
            platform.setPlatformCode(updatedPlatform.getPlatformCode());
        }
        if (updatedPlatform.getTaxNo() != null) {
            platform.setTaxNo(updatedPlatform.getTaxNo());
        }
        if (updatedPlatform.getState()!=null)
        {
            platform.setState(updatedPlatform.getState());
        }
        Platform savedPlatform=platformService.save(platform);
        return ResponseEntity.ok(savedPlatform);
        }
    return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id) {
        platformService.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
