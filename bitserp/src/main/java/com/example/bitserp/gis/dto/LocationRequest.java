package com.example.bitserp.gis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String type;

    private String address;
    private String city;
    private String state;
    private String country;

    private Double latitude;
    private Double longitude;
}
