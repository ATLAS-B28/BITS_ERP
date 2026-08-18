package com.example.bitserp.modules.finance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "revenue_projections")
@Setter
@Getter
public class RevenueProjection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "projection_date", nullable = false)
    private LocalDate projectionDate;

    @Column(name = "projected_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal projectedAmount;

    @Column(name = "actual_amount", precision = 14, scale = 2)
    private BigDecimal actualAmount;

    @Column(nullable = false, length = 30)
    private String method = "MOVING_AVG";

    @Column(name = "basis_months")
    private Integer basisMonths = 3;

    @Column(length = 80)
    private String region;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
