package com.example.bitserp.modules.procurement.entity;

import com.example.bitserp.shared.entity.BaseEntity;
import com.example.bitserp.shared.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@Setter
@Getter
public class PurchaseOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;

    @Column(name = "total_amount", precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by")
    private User raisedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @Column(length = 10)
    private String currency = "INR";

    @Column(name = "exchange_rate", precision = 14, scale = 6)
    private BigDecimal exchangeRate = BigDecimal.ONE;

    @Column(precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "cgst_amount", precision = 14, scale = 2)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_amount", precision = 14, scale = 2)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "igst_amount", precision = 14, scale = 2)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "tds_amount", precision = 14, scale = 2)
    private BigDecimal tdsAmount = BigDecimal.ZERO;

    @Column(name = "tax_total", precision = 14, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "grand_total", precision = 14, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "vendor_state", length = 80)
    private String vendorState;

    @Column(name = "tds_category", length = 20)
    private String tdsCategory = "goods";

    @Column(name = "is_interstate")
    private Boolean isInterstate = false;
}
