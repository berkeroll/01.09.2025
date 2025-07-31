package com.example.platform.Repository;

import com.example.platform.model.Investor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//import java.util.UUID;

public interface InvestorRepository extends JpaRepository<Investor, Long > {

    List<Investor> findByPlatformId(Long platformId);
}
