package com.example.platform.Repository;

import com.example.platform.model.Investor;

import com.example.platform.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

//import java.util.UUID;

public interface InvestorRepository extends JpaRepository<Investor, Long > {

    List<Investor> findByPlatformId(Long platformId);


    boolean existsBySicilNo(String sicilNo);
    boolean existsByMernis(String mernis);
    boolean existsByMernisAndPlatformId(String mernis, Long platformId);
    Optional<Investor> findFirstByMernis(String mernis);
    List<Investor>findByMernis(String mernis);
    boolean existsByMernisAndPlatform(String mernis, Platform platform);
    List<Investor> findByState(String state);
    List<Investor> findByPlatform(Platform platform);
    List<Investor> findByStatus(String  status);
    Optional<Investor> findBySicilNoAndPlatformId(String sicilNo,Long platformId);

}
