package com.example.bitserp.modules.finance.controller;

import com.example.bitserp.modules.finance.dto.FinanceSummaryResponse;
import com.example.bitserp.modules.finance.dto.LedgerEntryResponse;
import com.example.bitserp.modules.finance.dto.RevenueProjectionResponse;
import com.example.bitserp.modules.finance.service.LedgerService;
import com.example.bitserp.modules.finance.service.RevenueProjectionService;
import com.example.bitserp.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final LedgerService ledgerService;
    private final RevenueProjectionService revenueProjectionService;

    @GetMapping("/ledger")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<LedgerEntryResponse>>> getLedger() {
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.getAllEntries()));
    }

    @GetMapping("/ledger/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<LedgerEntryResponse>>> getByType(
            @PathVariable String type
    ) {
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.getByType(type)));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<FinanceSummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.getSummary()));
    }

    @PostMapping("/projections/calculate")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<RevenueProjectionResponse>> calculateProjection(
            @RequestParam(defaultValue = "3") int basisMonths
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                revenueProjectionService.projectNextMonth(basisMonths)
        ));
    }

    @GetMapping("/projections")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<RevenueProjectionResponse>>> getProjections() {
        return ResponseEntity.ok(ApiResponse.ok(
                revenueProjectionService.getFutureProjections()
        ));
    }

    @PatchMapping("/projections/{id}/actual")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<RevenueProjectionResponse>> updateActual(
            @PathVariable Integer id,
            @RequestParam BigDecimal amount
            ) {
        return ResponseEntity.ok(ApiResponse.ok(
                revenueProjectionService.updateActual(id, amount)
        ));
    }
}
