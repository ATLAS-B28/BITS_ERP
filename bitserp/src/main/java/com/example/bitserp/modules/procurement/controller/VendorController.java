package com.example.bitserp.modules.procurement.controller;

import com.example.bitserp.modules.procurement.dto.VendorRequest;
import com.example.bitserp.modules.procurement.dto.VendorResponse;
import com.example.bitserp.modules.procurement.service.VendorService;
import com.example.bitserp.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/procurement")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping("/vendors")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER')")
    public ResponseEntity<ApiResponse<VendorResponse>> createVendor(
            @RequestBody VendorRequest vendorRequest
            ) {
        return ResponseEntity.ok(ApiResponse.ok(vendorService.createVendor(vendorRequest)));
    }

    @GetMapping("/vendors")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER')")
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getAllVendors() {
        return ResponseEntity.ok(ApiResponse.ok(vendorService.getAllVendors()));
    }

    @GetMapping("/vendors/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER')")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorById(
            @PathVariable UUID id
            ) {
        return ResponseEntity.ok(ApiResponse.ok(vendorService.getVendorById(id)));
    }

    @PutMapping("/vendors/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER')")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(
            @PathVariable UUID id,
            @Valid @RequestBody VendorRequest request
            ) {
        return ResponseEntity.ok(ApiResponse.ok(vendorService.updateVendor(id, request)));
    }

    @DeleteMapping("/vendors/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER')")
    public ResponseEntity<ApiResponse<VendorResponse>> deleteVendor(
            @PathVariable UUID id
            ) {
        vendorService.deactivateVendor(id);
        return ResponseEntity.ok(ApiResponse.ok("Vendor deactivated", null));
    }
}
