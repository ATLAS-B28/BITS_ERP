package com.example.bitserp.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationStockSummary {
    private Integer locationId;
    private String locationName;
    private String locationType;
    private Double latitude;
    private Double longitude;
    private Integer totalQuantity;
    private Integer productCount;
    private Boolean hasLowStock;
}
