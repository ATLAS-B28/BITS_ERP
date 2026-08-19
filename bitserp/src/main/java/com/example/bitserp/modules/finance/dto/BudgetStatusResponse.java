package com.example.bitserp.modules.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BudgetStatusResponse {
    private String module;
    private BigDecimal allocated;
    private BigDecimal spent;
    private BigDecimal remaining;
    private double utilizationPercent;
    private String status;
}
