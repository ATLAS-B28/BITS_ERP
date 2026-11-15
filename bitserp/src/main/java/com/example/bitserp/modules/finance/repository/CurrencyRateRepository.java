package com.example.bitserp.modules.finance.repository;

import com.example.bitserp.modules.finance.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository
extends JpaRepository<CurrencyRate, Long> {
    Optional<CurrencyRate> findByCurrency(String currency);
    List<CurrencyRate> findAllByOrderByCurrencyAsc();
}
