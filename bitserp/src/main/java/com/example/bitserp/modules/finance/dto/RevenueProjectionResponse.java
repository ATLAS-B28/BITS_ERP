package com.example.bitserp.modules.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RevenueProjectionResponse {
    private Integer id;
    private LocalDate projectDate;
    private BigDecimal projectedAmount;
    private BigDecimal actualAmount;
    private String method;
    private Integer basisMonths;
    private String region;
}
