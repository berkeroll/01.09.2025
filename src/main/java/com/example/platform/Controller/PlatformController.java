package com.example.platform.Controller;

import com.example.platform.Repository.CryptoRepository;
import com.example.platform.Repository.InvestorRepository;
import com.example.platform.Repository.RoleRepository;
import com.example.platform.Service.PlatformService;
import com.example.platform.Service.UsersService;
import com.example.platform.dto.PlatformDto;
import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import com.example.platform.model.Role;
import com.example.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
//import java.util.UUID;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {

    private final PlatformService platformService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    CryptoRepository cryptoRepository;
    @Autowired
    UsersService usersService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    InvestorRepository investorRepository;



    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;

    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping // Tüm platformları listeleme
    public List<PlatformDto> getAllPlatforms() {
        // Aktif platformları çekiyoruz
        List<Platform> platforms = platformService.findGetAllStatus();
            // Platform listelerini DTO’ya çeviriyoruz
            List<PlatformDto> platformDtos = platforms.stream()
                    .map(platformService::convertToPlatformDto)
                    .collect(Collectors.toList());

            return platformDtos;
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}") //İD YE GÖRE PLATFORM LİSTELEME
    public ResponseEntity<PlatformDto> getPlatformById(@PathVariable Long id) {
        Optional<Platform> platformOptional = platformService.findById(id);

        if (platformOptional.isPresent()) {
            PlatformDto platformDto=platformService.convertToPlatformDto(platformOptional.get());
            return ResponseEntity.ok(platformDto);
        } else {
            return ResponseEntity.notFound().build(); // Eğer platform yoksa 404 döner
        }
    }
    @PreAuthorize("permitAll()")
    @PutMapping("/v1/{id}/refresh") //VAR OLAN APİKEYİ REFRESHLEME
    public ResponseEntity<?> refreshApiKey(@PathVariable Long id) {
        Optional<Platform> optionalPlatform = platformService.findById(id);

        if (optionalPlatform.isPresent()) {
            Platform platform = optionalPlatform.get();


            platform.setApiKey(UUID.randomUUID());

            Platform updatedPlatform = platformService.updatePlatform(platform);
            return ResponseEntity.ok("Yeni API Key oluşturuldu: " + updatedPlatform.getApiKey());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Platform bulunamadı: ID = " + id);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping  //YENİ PLATFORM OLUŞTURMA

    public ResponseEntity<PlatformDto> createPlatform(@RequestBody PlatformDto platformDto) {


        // DTO -> Entity
        Platform platform = new Platform();
        platform.setPlatformTitle(platformDto.getPlatformTitle());
        platform.setPlatformCode(platformDto.getPlatformCode());
        platform.setTaxNo(platformDto.getTaxNo());
        platform.setState(platformDto.getState());


        Platform savedPlatform = platformService.save(platform);

        // Entity -> DTO
        PlatformDto savedDto = platformService.convertToPlatformDto(savedPlatform);

        return ResponseEntity.ok(savedDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/{platformId}/cryptos/{cryptoId}") //PLATFORMA KRİPTO EKLEME
    public ResponseEntity<Platform> addCryptoToPlatform(
            @PathVariable Long platformId,
            @PathVariable Long cryptoId) {


        Platform updated = platformService.addCryptoToPlatform(platformId, cryptoId);
        return ResponseEntity.ok(updated);
    }
    @PreAuthorize("permitAll()")
    @PostMapping("/login") //APİKEY İLE TOKEN OLUŞTURULUR
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String apiKeyStr = body.get("apiKey");

        if (apiKeyStr == null || apiKeyStr.isEmpty()) {
            return ResponseEntity.badRequest().body("API Key zorunludur.");
        }

        try {
            UUID apiKey = UUID.fromString(apiKeyStr);

            Optional<Platform> platformOpt = platformService.findApi(apiKey);

            if (platformOpt.isPresent()) {
                Platform platform = platformOpt.get();

                List<String> roles = roleRepository.findAll()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());

                Date issuedAt = new Date();
                Date expiresAt = new Date(issuedAt.getTime() + JwtUtil.EXPIRATION_TIME);

                String jwt = jwtUtil.generateTokenWithApiKey(apiKey, platform, issuedAt, expiresAt, roles);

                Map<String, String> response = new HashMap<>();
                response.put("token", jwt);
                response.put("platformTitle", platform.getPlatformTitle());
                response.put("platformCode", platform.getPlatformCode());
                response.put("issuedAt", issuedAt.toString());
                response.put("expiresAt", expiresAt.toString());

                return ResponseEntity.ok(response);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz API Key");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("API Key formatı geçersiz (UUID bekleniyor)");
        }
    }


    @GetMapping("/secure") //TOKEN ÇALIŞIYOR MU DENEME ALANI
    public ResponseEntity<String> secureTest() {
        return ResponseEntity.ok("Token geçerli, korumalı alan");
    }



    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("update/{id}")
    public ResponseEntity<PlatformDto> updatePlatform(
            @PathVariable Long id,
            @RequestBody PlatformDto updatedDto) {

        Optional<Platform> platformOptional = platformService.findById(id);
        if (platformOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Platform platform = platformOptional.get();
        List<Investor> investors = investorRepository.findByPlatform(platform);

        // taxNo değiştirilemez kontrolü
        if (updatedDto.getTaxNo() != null && !updatedDto.getTaxNo().equals(platform.getTaxNo())) {
            throw new IllegalArgumentException("taxNo değiştirilemez");
        }


        if (updatedDto.getPlatformTitle() != null) {
            platform.setPlatformTitle(updatedDto.getPlatformTitle());
        }

        if (updatedDto.getPlatformCode() != null) {
            platform.setPlatformCode(updatedDto.getPlatformCode());
        }
        if (updatedDto.getState() != null) {
            platform.setState(updatedDto.getState());
            // state değiştiyse investor statülerini güncelle
            String status = platform.getState() ? "Aktif" : "Pasif";
            investors.forEach(inv -> inv.setStatus(status));
            investorRepository.saveAll(investors);
        }


        Platform savedPlatform = platformService.updatePlatform(platform);

        // DTO’ya çevir ve döndür
        PlatformDto savedDto = platformService.convertToPlatformDto(savedPlatform);

        return ResponseEntity.ok(savedDto);
    }



    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}") //İSTENİLEN PLATFORMUN SİLİNMESİ
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id) {
        platformService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}