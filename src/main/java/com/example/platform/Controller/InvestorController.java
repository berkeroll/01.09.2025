package com.example.platform.Controller;

import com.example.platform.Service.InvestorService;
import com.example.platform.Service.PlatformService;
import com.example.platform.dto.CryptoDto;
import com.example.platform.dto.InvestorDto;
import com.example.platform.model.Crypto;
import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//import java.util.UUID;

@RestController
@RequestMapping("/api/investors")
public class InvestorController {


    private final InvestorService investorService;
    private final PlatformService platformService;


    public InvestorController(InvestorService investorService, PlatformService platformService) {
        this.investorService = investorService;
        this.platformService = platformService;
    }

    @GetMapping //Kullanılacak //Kullanıldı
    public List<InvestorDto> getAllInvestors() {

        List<Investor> investors = investorService.findAll();

        List<InvestorDto> investorDtos = investors.stream()
                .map(investorService::convertToInvestorDto) // Service içindeki dönüşüm metodunu kullanıyoruz
                .collect(Collectors.toList());

        return investorDtos;
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<Investor> getInvestorById(@PathVariable Long id) {
//        return investorService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestorDto> getInvestorById(@PathVariable Long id) {
        return investorService.findById(id)
                .map(investorService::convertToInvestorDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/platform/{platformId}") //KULLANILACAK//KULLANILDI
    public List<InvestorDto> getInvestorsByPlatformId(@PathVariable Long platformId) {
        List<Investor> investors = investorService.findByPlatformId(platformId);

        List<InvestorDto> investorDtos = investors.stream()
                .map(investorService::convertToInvestorDto) // Entity → DTO
                .collect(Collectors.toList());

        return investorDtos;


    }

    @PostMapping("/checkmernis") //Kullanılacak //Kullanıldı
    public ResponseEntity<?> checkMernis(@RequestBody InvestorDto investorDto) {

        return investorService.checkInvestorByMernis(investorDto.getMernis());

    }


    @PostMapping("/saveinvestor")  //KULLANILACAK//KULLANILDI
    public ResponseEntity<InvestorDto> saveInvestor(@RequestBody InvestorDto investorDto,
                                                 @RequestHeader("Authorization") String token) {
        System.out.println("Token raw: '" + token + "'");
        Investor investor=new Investor();
        investor.setMernis(investorDto.getMernis());
        investor.setName(investorDto.getName());
        investor.setSurname(investorDto.getSurname());


        Investor saved = investorService.saveInvestor(investor, token);
        InvestorDto savedDto=investorService.convertToInvestorDto(saved);
        return ResponseEntity.ok(savedDto);
    }


    @PutMapping("/deactive/{id}")
    public ResponseEntity<Void> deleteInvestor(@PathVariable Long id) {
        investorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/active/{id}")
    public ResponseEntity<Void> activeById(@PathVariable Long id) {
        investorService.activeById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investor> updateInvestor(@PathVariable Long id, @RequestBody Investor updatedInvestor) {
        Optional<Investor> investorOptional = investorService.findById(id);

        if (investorOptional.isPresent()) {
            Investor investor = investorOptional.get();


            if (updatedInvestor.getName() != null) {
                investor.setName(updatedInvestor.getName());
            }
            if (updatedInvestor.getSurname() != null) {
                investor.setSurname(updatedInvestor.getSurname());
            }
            if (updatedInvestor.getMernis() != null) {
                investor.setMernis(updatedInvestor.getMernis());
            }


            if (updatedInvestor.getPlatform() == null) {

            } else {

                if (updatedInvestor.getPlatform().getId() != null) {
                    Platform platform = new Platform();
                    platform.setId(updatedInvestor.getPlatform().getId());
                    investor.setPlatform(platform);
                }
            }

            Investor savedInvestor = investorService.save(investor);
            return ResponseEntity.ok(savedInvestor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
