package com.example.platform.Controller;

import com.example.platform.Repository.InvestorRepository;
import com.example.platform.Repository.PlatformRepository;
import com.example.platform.Service.PlatformService;
import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import com.example.platform.Service.InvestorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
//import java.util.UUID;

@RestController
@RequestMapping("/api/investors")
public class InvestorController {



    private final InvestorService investorService;
    private final PlatformService platformService;




    public InvestorController(InvestorService investorService, PlatformService platformService ) {
        this.investorService = investorService;
        this.platformService=platformService;
    }

    @GetMapping
    public List<Investor> getAllInvestors() {
        return investorService.findAll();

    }

    @GetMapping("/{id}")
    public ResponseEntity<Investor> getInvestorById(@PathVariable Long id) {
        return investorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/platform/{platformId}")
    public List<Investor> getInvestorsByPlatformId(@PathVariable Long platformId) {
        return investorService.findByPlatformId(platformId);
    }


    @PostMapping("/add")
    private Investor createInvestor2(@RequestBody Investor investor)
    {
        return investorService.save(investor);


    }





    @PostMapping
    public Investor createInvestor(@Valid @RequestBody Investor investor) {
        if (investor
                .getPlatform() != null && investor.getPlatform().getId() != null) {
            Optional<Platform> platformOpt = platformService.findById(investor.getPlatform().getId());
            if (platformOpt.isPresent()) {
                investor.setPlatform(platformOpt.get());
            } else {
                throw new RuntimeException("Platform bulunamadı: " + investor.getPlatform().getId());
            }
        } else {
            throw new RuntimeException("Platform bilgisi eksik.");
        }
        return investorService.save(investor);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestor(@PathVariable Long  id) {
        investorService.deleteById(id);
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


//    public Investor save(Investor investor) {
//        if (investor.getPlatform() != null && investor.getPlatform().getId() != null) {
//            Optional<Platform> platformOpt = platformService.findById(investor.getPlatform().getId());
//            if (platformOpt.isPresent()) {
//                investor.setPlatform(platformOpt.get());
//            } else {
//                throw new RuntimeException("Platform bulunamadı: " + investor.getPlatform().getId());
//            }
//        }
//        return investorService.save(investor);
//    }






}
