package com.example.bitserp.modules.sales.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class SalesOrderItemRequest {
    @NotNull
    private UUID productId;
    @NotNull
    @Min(1)
    private Integer quantity;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal unitPrice;
}
