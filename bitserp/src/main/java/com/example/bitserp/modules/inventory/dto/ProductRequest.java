package com.example.bitserp.modules.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank
    @Size(max = 50)
    private String sku;

    @NotBlank
    @Size(max = 150)
    private String name;

    private String category;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal unitPrice;

    private String unitOfMeasure = "units";

    private String description;
}
