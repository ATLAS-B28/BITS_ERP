package com.example.bitserp.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String sku;
    private String name;
    private String category;
    private BigDecimal unitPrice;
    private String unitOfMeasure;
    private Boolean active;
}
