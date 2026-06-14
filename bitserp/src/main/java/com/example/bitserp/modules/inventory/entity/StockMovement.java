package com.example.bitserp.modules.inventory.entity;

import com.example.bitserp.shared.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private com.example.bitserp.shared.entity.Location location;

    @Column(name = "change_qty", nullable = false)
    private Integer changeQty;

    @Column(nullable = false, length = 50)
    private String reason;

    @Column(name = "reference_id")
    private UUID referenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moved_by")
    private User movedBy;

    @CreationTimestamp
    @Column(name = "moved_at", updatable = false)
    private OffsetDateTime movedAt;
}
