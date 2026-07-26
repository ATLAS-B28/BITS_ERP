package com.example.bitserp.gis.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearbyRequest {

    @NotNull
    private double latitude;
    @NotNull
    private double longitude;

    private Double radiusKm = 50.0;
    private String type;
}
