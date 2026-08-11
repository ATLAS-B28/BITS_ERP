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

    @Column(name = "delivery_coords", columnDefinition = "geometry(Point,4326")
    private Point deliveryCords;

    @Column(name = "total_amount", precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderItem> items = new ArrayList<>();
}
