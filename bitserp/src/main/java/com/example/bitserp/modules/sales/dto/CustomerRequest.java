package com.example.bitserp.modules.sales.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {
    @NotBlank
    private String name;
    @Email
    private String email;
    private String phone;
    private Integer locationId;
}
