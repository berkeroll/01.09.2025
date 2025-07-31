package com.example.platform.Service;

import com.example.platform.dto.CryptoDTO;
import com.example.platform.dto.PlatformResponseDTO;
import com.example.platform.model.Platform;
import com.example.platform.Repository.PlatformRepository;
import com.example.platform.model.PlatformCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public List<Platform> findAll() {
        return platformRepository.findAll();
    }

    public Optional<Platform> findById(Long id) {
        return platformRepository.findById(id);
    }

    public Platform save(Platform platform) {
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
        platformRepository.deleteById(id);
    }

    public Optional<PlatformResponseDTO> getPlatformWithCryptos(Long id) {
        Optional<Platform> platformOpt = platformRepository.findById(id);

        return platformOpt.map(platform -> {
            List<CryptoDTO> cryptos = platform.getPlatformCryptos().stream()
                    .map(PlatformCrypto::getCrypto)
                    .map(crypto -> new CryptoDTO(crypto.getCoins_title(), crypto.getStatus()))
                    .collect(Collectors.toList());



            return new PlatformResponseDTO(platform.getPlatformTitle(), cryptos);
        });

    }
}


