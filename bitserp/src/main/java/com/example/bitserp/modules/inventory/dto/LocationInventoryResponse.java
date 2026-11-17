package com.example.bitserp.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class LocationInventoryResponse {
    private Integer id;          // UUID if your Inventory id is a UUID
    private UUID productId;
    private String productName;
    private String sku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private Integer reorderLevel;
}