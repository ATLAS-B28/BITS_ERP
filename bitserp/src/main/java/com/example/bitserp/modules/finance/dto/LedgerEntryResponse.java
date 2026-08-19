package com.example.bitserp.modules.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class LedgerEntryResponse {
    private Integer id;
    private String type;
    private BigDecimal amount;
    private String description;
    private String referenceType;
    private UUID referenceId;
    private OffsetDateTime createdAt;
}
