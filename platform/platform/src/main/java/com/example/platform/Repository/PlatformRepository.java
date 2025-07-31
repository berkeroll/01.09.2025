package com.example.platform.Repository;

import com.example.platform.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;



public interface PlatformRepository extends JpaRepository<Platform, Long > {
    Optional<Platform> findByApiKey(UUID apiKey);
}
