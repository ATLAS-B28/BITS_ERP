package com.example.bitserp.modules.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class VendorResponse {

    private UUID id;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private String city;
    private String locationType;
    private String address;
    private Double lat;
    private Double lng;
    private Boolean active;
}
