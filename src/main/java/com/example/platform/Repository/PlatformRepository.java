package com.example.platform.Repository;

import com.example.platform.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



public interface PlatformRepository extends JpaRepository<Platform, Long > {
    Optional<Platform> findByApiKey(UUID apiKey);
    boolean existsByTaxNo(String taxNo);
    Optional<Platform>findByTaxNo(String taxNo);
    List<Platform> findByRecordStatus(String recordStatus);
    boolean existsByPlatformTitle(String platformTitle);
    boolean existsByPlatformCode(String platformCode);
    boolean existsByPlatformTitleAndRecordStatus(String platformTitle,String recordStatus);
    boolean existsByPlatformCodeAndRecordStatus(String platformCode,String recordStatus);
    boolean existsByTaxNoAndRecordStatus(String taxNo,String recordStatus);

}
