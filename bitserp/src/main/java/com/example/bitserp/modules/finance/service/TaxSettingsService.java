package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.entity.TaxSettings;
import com.example.bitserp.modules.finance.repository.TaxSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxSettingsService {

    private final TaxSettingsRepository taxSettingsRepository;

    @Transactional(readOnly = true)
    public List<TaxSettings> getAll() {
        return taxSettingsRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<TaxSettings> getByType(String type) {
        return taxSettingsRepository.findByType(type.toUpperCase());
    }

    @Transactional
    public List<TaxSettings> getByCountry(String country) {
        return taxSettingsRepository.findByCountry(country.toUpperCase());
    }

    @Transactional
    public TaxSettings updateRate(Integer id, BigDecimal rate, Boolean active) {
        TaxSettings tax = taxSettingsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tax settings not found" + id));
        if(rate != null) tax.setRate(rate);
        if(active != null) tax.setActive(active);

        return taxSettingsRepository.save(tax);
    }
}
