package com.example.bitserp.modules.finance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "budget_utilizations")
@Setter
@Getter
public class BudgetUtilization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(name = "reference_type", nullable = false, length = 30)
    private String referenceType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    private String note;

    @CreationTimestamp
    @Column(name = "recorded_at", updatable = false)
    private OffsetDateTime recordedAt;
}
