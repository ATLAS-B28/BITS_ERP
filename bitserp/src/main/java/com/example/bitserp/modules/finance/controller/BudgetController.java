package com.example.bitserp.modules.finance.controller;

import com.example.bitserp.modules.finance.dto.BudgetRequest;
import com.example.bitserp.modules.finance.dto.BudgetResponse;
import com.example.bitserp.modules.finance.dto.BudgetStatusResponse;
import com.example.bitserp.modules.finance.service.BudgetService;
import com.example.bitserp.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping("/budgets")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @Valid @RequestBody BudgetRequest request,
            @AuthenticationPrincipal String email
            ) {
        return ResponseEntity.ok(ApiResponse.ok(budgetService.createBudget(request, email)));
    }

    @PatchMapping("/budgets/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<BudgetResponse>> approveBudget(
            @PathVariable Integer id,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(ApiResponse.ok(budgetService.approveBudget(id, email)));
    }

    @PatchMapping("/budgets/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<BudgetResponse>> closeBudget(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(budgetService.closeBudget(id)));
    }

    @GetMapping("/budgets")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgets() {
        return ResponseEntity.ok(ApiResponse.ok(budgetService.getAllBudgets()));
    }

    @GetMapping("/budgets/module/{module}")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER','FIN_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgetsByModule(
            @PathVariable String module
    ) {
        return ResponseEntity.ok(ApiResponse.ok(budgetService.getBudgetsByModule(module)));
    }

    @GetMapping("/budgets/status/{module}")
    @PreAuthorize("hasAnyRole('ADMIN','FIN_MANAGER')")
    public ResponseEntity<ApiResponse<BudgetStatusResponse>> getBudgetStatus(
            @PathVariable String module
    ) {
        return ResponseEntity.ok(ApiResponse.ok(budgetService.getBudgetStatus(module)));
    }
}
