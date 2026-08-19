package com.example.bitserp.modules.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FinanceSummaryResponse {
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private BigDecimal netBalance;
    private BigDecimal totalRevenue;
    private BigDecimal totalBudgetAllocated;
    private BigDecimal totalBudgetSpent;
}
