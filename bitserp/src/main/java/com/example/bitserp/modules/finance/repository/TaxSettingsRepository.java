package com.example.bitserp.modules.finance.repository;

import com.example.bitserp.modules.finance.entity.TaxSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TaxSettingsRepository
extends JpaRepository<TaxSettings, Integer> {
    List<TaxSettings> findByActiveTrue();
    List<TaxSettings> findByType(String type);
    List<TaxSettings> findByCountry(String country);
    Optional<TaxSettings> findByTypeAndCategory(
            String type, String category
    );
    List<TaxSettings> findByTypeAndCountry(
            String type, String country
    );
}
