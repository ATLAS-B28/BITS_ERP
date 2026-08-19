package com.example.bitserp.modules.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BudgetRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String module;
    private String category;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal allocatedAmount;
    @NotNull
    private LocalDate periodStart;
    @NotNull
    private LocalDate periodEnd;
}
