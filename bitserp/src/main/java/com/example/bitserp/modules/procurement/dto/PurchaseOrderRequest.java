package com.example.bitserp.modules.procurement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PurchaseOrderRequest {

    @NotNull
    private UUID vendorId;

    @NotEmpty
    private List<PurchaseOrderItemRequest> items;
}
