package com.example.bitserp.gis.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationResponse {
   private Integer id;
   private String name;
   private String type;
   private String address;
   private String city;
   private String state;
   private String country;
   private Double latitude;
   private Double longitude;
   private Boolean active;
}
