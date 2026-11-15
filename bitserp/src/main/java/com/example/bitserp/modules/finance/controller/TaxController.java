package com.example.bitserp.modules.finance.controller;

import com.example.bitserp.modules.finance.entity.CurrencyRate;
import com.example.bitserp.modules.finance.entity.TaxSettings;
import com.example.bitserp.modules.finance.repository.CurrencyRateRepository;
import com.example.bitserp.modules.finance.repository.TaxSettingsRepository;
import com.example.bitserp.modules.finance.service.CurrencyService;
import com.example.bitserp.modules.finance.service.TaxSettingsService;
import com.example.bitserp.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance/tax")
@RequiredArgsConstructor
public class TaxController {

    private final TaxSettingsService taxSettingsService;
    private final CurrencyService currencyService;

    @GetMapping("/rates")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE'," +
            "'PROC_MANAGER','SALES_MANAGER','ACCOUNTS')")
    public ResponseEntity<ApiResponse<List<TaxSettings>>> getAllTaxesRates() {
        return ResponseEntity.ok(ApiResponse.ok(
                taxSettingsService.getAll()
        ));
    }

    @GetMapping("/rates/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE'," +
            "'PROC_MANAGER','SALES_MANAGER','ACCOUNTS')")
    public ResponseEntity<ApiResponse<List<TaxSettings>>> getByType(
            @PathVariable String type
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                taxSettingsService.getByType(type)
        ));
    }

    @GetMapping("/rates/country/{country}")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE'," +
            "'PROC_MANAGER','SALES_MANAGER','ACCOUNTS')")
    public ResponseEntity<ApiResponse<List<TaxSettings>>> getByCountry(
            @PathVariable String country
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                taxSettingsService.getByCountry(country)
        ));
    }

    @PutMapping("/rates/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<TaxSettings>> updateTaxRate(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body
            ) {
        BigDecimal rate = body.containsKey("rate")
                ? new BigDecimal(body.get("rate").toString()) : null;
        Boolean active = body.containsKey("active")
                ? Boolean.parseBoolean(body.get("active").toString()) : null;
        return ResponseEntity.ok(ApiResponse.ok(
                taxSettingsService.updateRate(id, rate, active)
        ));
    }

    @GetMapping("/currencies")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE'," +
            "'PROC_MANAGER','SALES_MANAGER','ACCOUNTS')")
    public ResponseEntity<ApiResponse<List<CurrencyRate>>> getCurrencies() {
        return ResponseEntity.ok(ApiResponse.ok(
                currencyService.getAllCurrencies()
        ));
    }

    @PutMapping("/currencies/{code}")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<CurrencyRate>> updateCurrencyRate(
            @PathVariable String code,
            @RequestBody Map<String, Object> body
    ) {
        BigDecimal rate = new BigDecimal(
                body.get("rateToInr").toString()
        );

        return ResponseEntity.ok(ApiResponse.ok(
                currencyService.updateRate(code, rate)
        ));
    }

    @GetMapping("/convert")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> convert(
            @RequestParam BigDecimal amount,
            @RequestParam String from,
            @RequestParam String to
    ) {
        BigDecimal converted = currencyService.convert(amount, from, to);
        BigDecimal rate = currencyService.getRate(from, to);
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "from", from.toUpperCase(),
                "to", to.toUpperCase(),
                "originalAmount", amount,
                "convertedAmount", converted,
                "rate", rate
        )));
    }
}