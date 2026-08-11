package com.example.bitserp.modules.sales.controller;

import com.example.bitserp.modules.sales.dto.CustomerRequest;
import com.example.bitserp.modules.sales.dto.CustomerResponse;
import com.example.bitserp.modules.sales.service.CustomerService;
import com.example.bitserp.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/customers")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerRequest request
            ) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.createCustomer(request)));
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(
            @PathVariable UUID id
            ) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getCustomerById(id)));
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAllCustomers()));
    }
}
