package com.example.bitserp.modules.procurement.dto;

import com.example.bitserp.modules.procurement.entity.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PurchaseOrderResponse {

    private UUID id;
    private String vendorName;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private String raisedBy;
    private List<PurchaseOrderItemResponse> items;
    private OffsetDateTime createdAt;
}
