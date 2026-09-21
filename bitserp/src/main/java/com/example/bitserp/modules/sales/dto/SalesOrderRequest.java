package com.example.bitserp.modules.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SalesOrderRequest {
    @NotNull
    private UUID customerId;
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    @NotEmpty
    @Valid
    private List<SalesOrderItemRequest> items;
}
