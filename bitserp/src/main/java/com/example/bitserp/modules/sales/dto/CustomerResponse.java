package com.example.bitserp.modules.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String city;
}

