package com.example.platform.Service;

import com.example.platform.Repository.CryptoRepository;
import com.example.platform.Repository.InvestorRepository;
import com.example.platform.dto.CryptoDto;
import com.example.platform.dto.InvestorDto;
import com.example.platform.dto.PlatformDto;
import com.example.platform.model.Crypto;
import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import com.example.platform.Repository.PlatformRepository;
import com.example.platform.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlatformService {

    @Autowired
    private CryptoRepository cryptoRepository;

    @Autowired
    private final PlatformRepository platformRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    InvestorRepository investorRepository;



    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }


    public List<Platform> findGetAllStatus() {
        return platformRepository.findByRecordStatus("A");
    }

    public Optional<Platform> findById(Long id) {
        return platformRepository.findById(id);
    }




    // Platform → PlatformDto
    public PlatformDto convertToPlatformDto(Platform platform) {
        PlatformDto dto = new PlatformDto();
        dto.setId(platform.getId());
        dto.setPlatformTitle(platform.getPlatformTitle());
        dto.setPlatformCode(platform.getPlatformCode());
        dto.setTaxNo(platform.getTaxNo());
        dto.setState(platform.getState());
        dto.setRecordStatus(platform.getRecord_status());
        dto.setApiKey(platform.getApiKey());

        // Investor listesi
        if (platform.getInvestors() != null) {
            List<InvestorDto> investorDtos = platform.getInvestors().stream()
                    .map(this::convertToInvestorDtoWithoutPlatform)
                    .collect(Collectors.toList());
            dto.setInvestors(investorDtos);
        } else {
            dto.setInvestors(Collections.emptyList());
        }

        // Crypto listesi
        if (platform.getCryptos() != null) {
            Set<CryptoDto> cryptoDtos = platform.getCryptos().stream()
                    .map(this::convertToCryptoDtoWithoutPlatformsAndInvestors)
                    .collect(Collectors.toSet());
            dto.setCryptos(cryptoDtos);
        } else {
            dto.setCryptos(Collections.emptySet());
        }

        return dto;
    }






    // Platform DTO (Investor ve Crypto alanları boş)
    private PlatformDto convertToPlatformDtoWithoutInvestorsAndCryptos(Platform platform) {
        PlatformDto dto = new PlatformDto();
        dto.setId(platform.getId());
        dto.setPlatformTitle(platform.getPlatformTitle());
        dto.setPlatformCode(platform.getPlatformCode());
        dto.setTaxNo(platform.getTaxNo());
        dto.setState(platform.getState());
        dto.setInvestors(Collections.emptyList());
        dto.setCryptos(Collections.emptySet());
        return dto;
    }

    // Investor DTO (Platform ve Crypto alanları boş)
    private InvestorDto convertToInvestorDtoWithoutPlatform(Investor investor) {
        InvestorDto dto = new InvestorDto();
        dto.setId(investor.getId());
        dto.setName(investor.getName());
        dto.setSurname(investor.getSurname());
        dto.setMernis(investor.getMernis());
        dto.setPlatformName(investor.getPlatform() != null ? investor.getPlatform().getPlatformTitle() : null);
        dto.setPlatform(Collections.emptyList());
        dto.setCryptos(Collections.emptySet());
        return dto;
    }

    // Investor DTO (Platform ve Crypto boş) farklı helper
    private InvestorDto convertToInvestorDtoWithoutPlatformAndCryptos(Investor investor) {
        InvestorDto dto = new InvestorDto();
        dto.setId(investor.getId());
        dto.setName(investor.getName());
        dto.setSurname(investor.getSurname());
        dto.setMernis(investor.getMernis());
        dto.setPlatformName(investor.getPlatform() != null ? investor.getPlatform().getPlatformTitle() : null);
        dto.setPlatform(Collections.emptyList());
        dto.setCryptos(Collections.emptySet());
        return dto;
    }

    // Crypto DTO (Platform ve Investor boş)
    private CryptoDto convertToCryptoDtoWithoutPlatformsAndInvestors(Crypto crypto) {
        CryptoDto dto = new CryptoDto();
        dto.setId(crypto.getId());
        dto.setCoinsTitle(crypto.getCoinsTitle());
        dto.setCoinsCode(crypto.getCoinsCode());
        dto.setCoinsTax(crypto.getCoinsTax());
        dto.setStatus(crypto.getStatus());
        dto.setPlatforms(Collections.emptySet());
        dto.setInvestors(Collections.emptySet());
        return dto;
    }




    public Platform save(Platform platform) {

        if (platformRepository.existsByPlatformTitleAndRecordStatus(platform.getPlatformTitle(),"A")) {
            throw new IllegalArgumentException("Platform Title zaten mevcut");
        } else if (platformRepository.existsByPlatformCodeAndRecordStatus(platform.getPlatformCode(),"A")) {
            throw new IllegalArgumentException("Platform Code zaten mevcut");
        }
        else if (platformRepository.existsByTaxNoAndRecordStatus(platform.getTaxNo(),"A")) {
            throw new IllegalArgumentException("Bu TaxNo başka bir platforma ait.");
        }


        platform.setApiKey(UUID.randomUUID());
        return platformRepository.save(platform);
    }


    public Platform updatePlatform(Platform platform) {
        return platformRepository.save(platform);
    }

    public Optional<Platform> findApi(UUID apiKey) {
        return platformRepository.findByApiKey(apiKey);
    }

    public Platform apiUpdate(Long id, Platform yeniVeri) {
        Optional<Platform> eski = platformRepository.findById(id);
        if (eski.isPresent()) {
            Platform k = eski.get();
            k.setApiKey(yeniVeri.getApiKey());

            return platformRepository.save(k);
        } else {
            throw new RuntimeException("Kullanıcı bulunamadı");
        }
    }

    public void deleteById(Long id) {
        Optional<Platform> platformOpt = platformRepository.findById(id);
        if (platformOpt.isPresent()) {
            Platform platform = platformOpt.get();
            if (platform.getRecord_status().equals("A")) {
                platform.setRecord_status("D");
                platformRepository.save(platform);
            } else if (platform.getRecord_status().equals("D")) {
                throw new IllegalArgumentException("Bu kullanıcı zaten pasif durumda");
            }
        }
    }
    // Platform'a crypto ekleme
    @Transactional
    public Platform addCryptoToPlatform(Long platformId, Long cryptoId) {

        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new RuntimeException("Platform bulunamadı: " + platformId));


        Crypto crypto = cryptoRepository.findById(cryptoId)
                .orElseThrow(() -> new RuntimeException("Crypto bulunamadı: " + cryptoId));

        // Daha önce eklenmiş mi kontrol et
        if (platform.getCryptos().contains(crypto)) {
            throw new RuntimeException("Bu platforma bu crypto zaten eklenmiş!");
        }

        // İlişkiye ekle
        platform.getCryptos().add(crypto);

        // (çift yönlü ilişki senkronizasyonu)
        crypto.getPlatforms().add(platform);

        return platformRepository.save(platform);
    }


}