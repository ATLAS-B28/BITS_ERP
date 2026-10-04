package com.example.bitserp.gis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnrichedLocationResponse {

        private Integer id;
        private String name;
        private String type;           // warehouse | vendor_site | store | customer_site
        private String ownerName;      // vendor name / customer name / null for warehouses
        private String ownerType;      // VENDOR | CUSTOMER | INTERNAL
        private String address;
        private String city;
        private String state;
        private Double latitude;
        private Double longitude;
        private Boolean active;
        private Boolean hasLowStock;   // only for warehouses
        private Integer totalStock;    // only for warehouses

}
