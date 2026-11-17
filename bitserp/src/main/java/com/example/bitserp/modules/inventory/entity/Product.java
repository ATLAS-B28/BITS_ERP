package com.example.bitserp.modules.inventory.entity;

import com.example.bitserp.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String sku;

    @Column(length = 50)
    private String name;

    @Column(length = 80)
    private String category;

    @Column(name = "item_type", nullable = false, length = 10)
    private String itemType = "PRODUCT";

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure = "units";

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "hsn_code", length = 20)
    private String hsnCode;

    @Column(name = "gst_rate", precision = 6, scale = 4)
    private BigDecimal gstRate = BigDecimal.valueOf(18);

    @Column(name = "gst_category", length = 20)
    private String gstCategory = "goods_services";
}
