package com.example.bitserp.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserRequest {
    private String roleName;
    private String status;
}
