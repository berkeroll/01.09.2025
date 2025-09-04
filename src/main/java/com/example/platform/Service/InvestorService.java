package com.example.platform.Service;

import com.example.platform.Repository.CryptoRepository;
import com.example.platform.Repository.InvestorRepository;
import com.example.platform.Repository.PlatformRepository;
import com.example.platform.dto.CryptoDto;
import com.example.platform.dto.InvestorDto;
import com.example.platform.dto.PlatformDto;
import com.example.platform.model.Crypto;
import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import com.example.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvestorService {

    private final InvestorRepository investorRepository;
    private final PlatformRepository platformRepository;
    private JwtUtil jwtUtil;
    @Autowired
    CryptoRepository cryptoRepository;
    @Autowired
    PlatformService platformService;




    private Random random = new Random();

    public InvestorService(InvestorRepository investorRepository, PlatformRepository platformRepository, JwtUtil jwtUtil) {
        this.investorRepository = investorRepository;
        this.platformRepository = platformRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<Investor> findAll() {
        List<Investor> investors = investorRepository.findByState("A");
        for (Investor inv : investors) {

            if (inv.getPlatform() != null) {
                inv.setPlatformName(inv.getPlatform().getPlatformTitle());
            }
        }


        return investors;
    }

    public Optional<Investor> findById(Long id) {
        return investorRepository.findById(id);
    }





    // Investor → InvestorDto
    public InvestorDto convertToInvestorDto(Investor investor) {
        InvestorDto dto = new InvestorDto();
        dto.setId(investor.getId());
        dto.setName(investor.getName());
        dto.setSurname(investor.getSurname());
        dto.setMernis(investor.getMernis());
        dto.setState(investor.getState());
        dto.setStatus(investor.getStatus());
        dto.setSicilNo(investor.getSicilNo());
        dto.setPlatformName(investor.getPlatform() != null ? investor.getPlatform().getPlatformTitle() : null);

        // Platform listesi (tek platformu listeye ekliyoruz)
        List<PlatformDto> platformDtos = new ArrayList<>();
        if (investor.getPlatform() != null) {
            platformDtos.add(convertToPlatformDtoWithoutInvestorsAndCryptos(investor.getPlatform()));
        }
        dto.setPlatform(platformDtos); // Boş listeyi de destekliyor

        // Crypto listesi
        Set<CryptoDto> cryptoDtos = new HashSet<>();
        if (investor.getCryptos() != null) {
            cryptoDtos = investor.getCryptos().stream()
                    .map(this::convertToCryptoDtoWithoutPlatformsAndInvestors)
                    .collect(Collectors.toSet());
        }
        dto.setCryptos(cryptoDtos);

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





    public Investor save(Investor investor) {
        Platform platform = platformRepository.findById(investor.getPlatform().getId())
                .orElseThrow(() -> new RuntimeException("Platform bulunamadı"));
        if (platform.getState() == false) {
            throw new IllegalArgumentException("Platform Pasif durumda");
        }

        return investorRepository.save(investor);
    }




    public void deleteById(Long id) {
        Optional<Investor> investorOpt = investorRepository.findById(id);
        if (investorOpt.isPresent()) {
            Investor investor = investorOpt.get();
            if (investor.getState().equals("A")) {
                investor.setState("D");
                investorRepository.save(investor);
            } else if (investor.getState().equals("D")) {
                throw new IllegalArgumentException("Bu kullanıcı zaten pasif durumda");
            }


        } else {
            throw new IllegalArgumentException("ID bulunamadı");
        }


    }

    public void activeById(Long id)
    {
        Optional<Investor> investorOpt = investorRepository.findById(id);
        if (investorOpt.isPresent()) {
            Investor investor = investorOpt.get();
            if (investor.getState().equals("D")) {
                investor.setState("A");
                investorRepository.save(investor);
            } else if (investor.getState().equals("A")) {
                throw new IllegalArgumentException("Bu kullanıcı zaten aktif durumda");
            }


        } else {
            throw new IllegalArgumentException("ID bulunamadı");
        }
    }

    public List<Investor> findByPlatformId(Long platformId) {
        return investorRepository.findByPlatformId(platformId);

    }

    public String generateUniqueSicilNo() {
        String sicilNo;
        do {
            sicilNo = String.format("%08d", random.nextInt(100_000_000));
        }
        while (investorRepository.existsBySicilNo(sicilNo));
        return sicilNo;
    }



    public Investor saveInvestor(Investor investor, String token) {

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

        Long platformId = jwtUtil.extractPlatformId(token);
        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new RuntimeException("Platform bulunamadı"));
        if (platform.getState() == false) {
            throw new IllegalArgumentException("Platform Pasif Durumda");
        }
        investor.setPlatformName(platform.getPlatformTitle());

        String mernis = investor.getMernis();
        if (mernis != null) {
            mernis = mernis.trim();
        } else {
            throw new RuntimeException("Mernis bilgisi eksik");
        }

        // Aynı mernis + platform kaydı var mı kontrol et
        if (investorRepository.existsByMernisAndPlatform(mernis, platform)) {
            throw new IllegalArgumentException(platform.getPlatformTitle() + " platformunda bu Mernis zaten kayıtlı.");
        }

        // Sistemde aynı mernis ile herhangi bir kayıt var mı kontrol et
        Optional<Investor> existingInvestorOpt = investorRepository.findFirstByMernis(mernis);

        if (existingInvestorOpt.isPresent()) {
            // Aynı mernis var, sicilNo'yu al, yeni kayıt oluştur platform ile birlikte
            Investor existingInvestor = existingInvestorOpt.get();
            if (!existingInvestor.getName().equals(investor.getName()) || !existingInvestor.getSurname().equals(investor.getSurname()))
            {
                throw new IllegalArgumentException("Bu merniste bu ad ve soyada sahip kullanıcı mevcut değiştirilemez.");

            }
            investor.setSicilNo(existingInvestor.getSicilNo()); // Mevcut sicilNo'yu kullan
            investor.setPlatform(platform);
            investor.setMernis(mernis);

            return investorRepository.save(investor);

        } else {
            // Yeni mernis, yeni sicilNo üret ve kayıt oluştur
            investor.setPlatform(platform);
            investor.setSicilNo(generateUniqueSicilNo());
            investor.setMernis(mernis);

            return investorRepository.save(investor);
        }
    }
    public ResponseEntity<?> checkInvestorByMernis(String mernis) {
        List<Investor> investors = investorRepository.findByMernis(mernis);

        if (!investors.isEmpty()) {
            Investor firstInvestor = investors.get(0);


            InvestorDto response = new InvestorDto();
            response.setName(firstInvestor.getName());
            response.setSurname(firstInvestor.getSurname());

            // Tüm platformları ekle
            List<PlatformDto> platformList = new ArrayList<>();
            for (Investor inv : investors) {
                Platform platform = inv.getPlatform();
                PlatformDto dto = new PlatformDto();
                dto.setId(platform.getId());
                dto.setPlatformTitle(platform.getPlatformTitle());
                platformList.add(dto);
            }

            response.setPlatform(platformList);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok().body("Bu mernis ile investor bulunamadı");
    }







}




