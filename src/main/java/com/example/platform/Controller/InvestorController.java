package com.example.platform.Controller;

import com.example.platform.Repository.PlatformRepository;
import com.example.platform.Service.InvestorService;
import com.example.platform.Service.PlatformService;
import com.example.platform.dto.CryptoDto;
import com.example.platform.dto.InvestorDto;
import com.example.platform.model.Crypto;
import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private PlatformRepository platformRepository;


    public InvestorController(InvestorService investorService, PlatformService platformService) {
        this.investorService = investorService;
        this.platformService = platformService;
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping //Tüm İnvestorları Listeleme
    public List<InvestorDto> getAllInvestors() {

        List<Investor> investors = investorService.findAll();

        List<InvestorDto> investorDtos = investors.stream()
                .map(investorService::convertToInvestorDto)
                .collect(Collectors.toList());

        return investorDtos;
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}") //ID ye göre investor listeleme
    public ResponseEntity<InvestorDto> getInvestorById(@PathVariable Long id) {
        return investorService.findById(id)
                .map(investorService::convertToInvestorDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/platform/{platformId}") //Belirlitilen Platforma kayıtlı olan investorları listeleme
    public List<InvestorDto> getInvestorsByPlatformId(@PathVariable Long platformId) {
        List<Investor> investors = investorService.findByPlatformId(platformId);

        List<InvestorDto> investorDtos = investors.stream()
                .map(investorService::convertToInvestorDto) // Entity → DTO
                .collect(Collectors.toList());

        return investorDtos;

    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/checkmernis") //Sistemde bu tc kimlik ile yatırımcı var mı kontrol
    public ResponseEntity<?> checkMernis(@RequestBody InvestorDto investorDto) {

        return investorService.checkInvestorByMernis(investorDto.getMernis());

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("{id}/saveinvestor") //Platform Seçilerek yatırımcı kayıt etme
    public ResponseEntity<InvestorDto> saveInvestor(@RequestBody InvestorDto investorDto,@PathVariable Long id) {

        Investor investor=new Investor();
        investor.setMernis(investorDto.getMernis());
        investor.setName(investorDto.getName());
        investor.setSurname(investorDto.getSurname());


        Investor saved = investorService.saveInvestor(investor, id);
        InvestorDto savedDto=investorService.convertToInvestorDto(saved);
        return ResponseEntity.ok(savedDto);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/deactive/{id}") //Kullanıcı silme (soft delete)
    public ResponseEntity<Void> deleteInvestor(@PathVariable Long id) {
        investorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/active/{id}")
    public ResponseEntity<Void> activeById(@PathVariable Long id) {
        investorService.activeById(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")  //Kullanıcı Bilgileri Güncelleme //Frontend tarafında kullanılmadı
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
