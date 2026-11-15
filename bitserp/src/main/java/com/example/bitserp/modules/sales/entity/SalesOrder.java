package com.example.bitserp.modules.sales.entity;

import com.example.bitserp.shared.entity.BaseEntity;
import com.example.bitserp.shared.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
@Getter
@Setter
public class SalesOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private SalesOrderStatus status = SalesOrderStatus.PENDING;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "delivery_coords", columnDefinition = "geography(Point,4326)")
    private Point deliveryCords;

    @Column(name = "total_amount", precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderItem> items = new ArrayList<>();

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

    @Column(name = "tax_total", precision = 14, scale = 2)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "grand_total", precision = 14, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "customer_country", length = 10)
    private String customerCountry = "IN";

    @Column(name = "customer_state", length = 80)
    private String customerState;

    @Column(name = "is_interstate")
    private Boolean isInterstate = false;

    @Column(name = "is_export")
    private Boolean isExport = false;
}
