package com.example.bitserp.modules.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SalesOrderItemResponse {
    private Integer id;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private  BigDecimal totalPrice;
}
