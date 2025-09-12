package com.example.platform.Service;

import com.example.platform.Repository.CryptoRepository;
import com.example.platform.Repository.PlatformRepository;
import com.example.platform.dto.CryptoDto;
import com.example.platform.dto.InvestorDto;
import com.example.platform.dto.PlatformDto;
import com.example.platform.model.Crypto;
import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class CryptoService {

    private final CryptoRepository cryptoRepository;

    private final PlatformRepository platformRepository;

    public Crypto addCrypto(Crypto crypto)
    {
        if (cryptoRepository.existsByCoinsTitle(crypto.getCoinsTitle()))
        {
            throw new IllegalArgumentException("Bu crypto adı zaten mevcut");

        }
        else if (cryptoRepository.existsByCoinsCode(crypto.getCoinsCode()))
        {
            throw new IllegalArgumentException("Bu crypto code zaten mevcut");

        }
        else if (cryptoRepository.existsByCoinsTax(crypto.getCoinsTax()))
        {
            throw new IllegalArgumentException("Bu crypto tax zaten mevcut");

        }
        Crypto savedCrypto=cryptoRepository.save(crypto);
        return savedCrypto;

    }



    // Crypto → CryptoDto
    public CryptoDto convertToCryptoDto(Crypto crypto) {
        CryptoDto dto = new CryptoDto();
        dto.setId(crypto.getId());
        dto.setCoinsTitle(crypto.getCoinsTitle());
        dto.setCoinsCode(crypto.getCoinsCode());
        dto.setCoinsTax(crypto.getCoinsTax());
        dto.setStatus(crypto.getStatus());

        // Platform set
        if (crypto.getPlatforms() != null) {
            Set<PlatformDto> platformDtos = crypto.getPlatforms().stream()
                    .map(this::convertToPlatformDtoWithoutInvestorsAndCryptos)
                    .collect(Collectors.toSet());
            dto.setPlatforms(platformDtos);
        } else {
            dto.setPlatforms(Collections.emptySet());
        }

        // Investor set
        if (crypto.getInvestors() != null) {
            Set<InvestorDto> investorDtos = crypto.getInvestors().stream()
                    .map(this::convertToInvestorDtoWithoutPlatformAndCryptos)
                    .collect(Collectors.toSet());
            dto.setInvestors(investorDtos);
        } else {
            dto.setInvestors(Collections.emptySet());
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





    public Crypto updatedSaveCrypto(Crypto crypto)
    {
        Crypto savedCrypto=cryptoRepository.save(crypto);
        return savedCrypto;
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




