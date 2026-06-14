package com.example.bitserp.modules.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockUpdateRequest {

    @NotNull
    private Integer locationId;

    @NotNull
    private Integer changeQty;

    @NotBlank
    private String reason;
}
