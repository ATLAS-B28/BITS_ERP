package com.example.bitserp.modules.finance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tax_settings")
@Setter
@Getter
public class TaxSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String type;

    @Column(nullable = false, length = 100)
    private BigDecimal rate;

    @Column(nullable = false, length = 100)
    private String country = "IN";

    @Column(length =  10)
    private String category;

    @Column(nullable = false)
    private Boolean active = true;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
