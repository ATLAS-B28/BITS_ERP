package com.example.bitserp.modules.finance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "currency_rates")
@Getter
@Setter
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 10)
    private String currency;

    @Column(nullable = false, length = 5)
    private String symbol;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "rate_to_inr", nullable = false
    ,precision = 14, scale = 6)
    private BigDecimal rateToInr;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
