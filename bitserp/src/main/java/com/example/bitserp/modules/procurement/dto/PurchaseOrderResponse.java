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
    UUID id;
    String vendorName;
    PurchaseOrderStatus status;
    BigDecimal subtotal;
    BigDecimal totalAmount;
    BigDecimal grandTotal;
    BigDecimal cgstAmount;
    BigDecimal sgstAmount;
    BigDecimal igstAmount;
    BigDecimal tdsAmount;
    BigDecimal taxTotal;
    String currency;
    BigDecimal exchangeRate;
    Boolean isInterstate;
    String tdsCategory;
    String cancellationReason;
    String raisedBy;
    String approvedBy;
    List<PurchaseOrderItemResponse> items;  // ← items is 19th
    OffsetDateTime createdAt;
};
