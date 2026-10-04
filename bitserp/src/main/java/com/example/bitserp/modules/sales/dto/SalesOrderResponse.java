package com.example.bitserp.modules.sales.dto;

import com.example.bitserp.modules.sales.entity.SalesOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SalesOrderResponse {
    private UUID id;
    private String customerName;
    private SalesOrderStatus status;
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private BigDecimal totalAmount;
    private String createdBy;
    private List<SalesOrderItemResponse> items;
    private OffsetDateTime createdAt;
}
