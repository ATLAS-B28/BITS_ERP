package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.entity.CurrencyRate;
import com.example.bitserp.modules.finance.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRateRepository currencyRateRepository;

    public BigDecimal toINR(BigDecimal amount, String fromCurrency) {
        if(amount == null) return BigDecimal.ZERO;
        if("INR".equalsIgnoreCase(fromCurrency)) return amount;
        return currencyRateRepository
                .findByCurrency(fromCurrency.toUpperCase())
                .map(r -> amount.multiply(r.getRateToInr())
                        .setScale(2, RoundingMode.HALF_UP))
                .orElse(amount);
    }

    public BigDecimal fromINR(BigDecimal inrAmount, String toCurrency) {
        if(inrAmount == null) return BigDecimal.ZERO;
        if("INR".equalsIgnoreCase(toCurrency)) return inrAmount;
        return currencyRateRepository
                .findByCurrency(toCurrency.toUpperCase())
                .map(r -> inrAmount.divide(
                        r.getRateToInr(), 2, RoundingMode.HALF_UP
                ))
                .orElse(inrAmount);
    }

    public BigDecimal convert(BigDecimal amount, String from, String to) {
        BigDecimal inr = toINR(amount, from);
        return fromINR(inr, to);
    }

    public BigDecimal getRate(String from, String to) {
        BigDecimal fromRate = getToINRRate(from);
        BigDecimal toRate = getToINRRate(to);
        return fromRate.divide(toRate, 6, RoundingMode.HALF_UP);
    }

    public List<CurrencyRate> getAllCurrencies() {
        return currencyRateRepository.findAllByOrderByCurrencyAsc();
    }


    public Map<String, BigDecimal> getAllRates() {
        return currencyRateRepository.findAllByOrderByCurrencyAsc()
                .stream()
                .collect(Collectors.toMap(
                        CurrencyRate::getCurrency,
                        CurrencyRate::getRateToInr
                ));
    }

    @Transactional
    public CurrencyRate updateRate(String currency, BigDecimal newRate) {
        CurrencyRate rate = currencyRateRepository
                .findByCurrency(currency.toUpperCase())
                .orElseThrow(() -> new RuntimeException(
                        "Currency " + currency.toUpperCase() + " not found."
                ));
        rate.setRateToInr(newRate);
        return currencyRateRepository.save(rate);
    }

    private BigDecimal getToINRRate(String currency) {
        if("INR".equalsIgnoreCase(currency))
            return BigDecimal.ONE;

        return currencyRateRepository
                .findByCurrency(currency.toUpperCase())
                .map(CurrencyRate::getRateToInr)
                .orElse(BigDecimal.ONE);
    }
}
