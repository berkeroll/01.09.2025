package com.example.platform.Service;

import com.example.platform.model.Investor;
import com.example.platform.model.Platform;
import com.example.platform.Repository.InvestorRepository;
import com.example.platform.Repository.PlatformRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InvestorService {

    private final InvestorRepository investorRepository;
    private final PlatformRepository platformRepository;

    public InvestorService(InvestorRepository investorRepository, PlatformRepository platformRepository) {
        this.investorRepository = investorRepository;
        this.platformRepository = platformRepository;
    }

    public List<Investor> findAll() {
        return investorRepository.findAll();
    }

    public Optional<Investor> findById(Long id) {
        return investorRepository.findById(id);
    }

    public Investor save(Investor investor) {
        return investorRepository.save(investor);
    }

    public void deleteById(Long id) {
        investorRepository.deleteById(id);
    }

    public List<Investor> findByPlatformId(Long platformId) {
        return investorRepository.findByPlatformId(platformId);

    }





}
