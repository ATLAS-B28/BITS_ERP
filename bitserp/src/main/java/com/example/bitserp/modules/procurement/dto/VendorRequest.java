package com.example.bitserp.modules.procurement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VendorRequest {

    @NotBlank
    private String name;

    @Email
    private String contactEmail;

    private String contactPhone;
    private Integer locationId;
}
