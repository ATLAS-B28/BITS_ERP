package com.example.bitserp.modules.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BudgetResponse {
    private Integer id;
    private String name;
    private String module;
    private String category;
    private BigDecimal allocatedAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String status;
}
