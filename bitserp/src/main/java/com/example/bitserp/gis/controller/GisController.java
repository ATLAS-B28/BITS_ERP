package com.example.bitserp.gis.controller;

import com.example.bitserp.gis.dto.LocationRequest;
import com.example.bitserp.gis.dto.LocationResponse;
import com.example.bitserp.gis.dto.NearbyRequest;
import com.example.bitserp.gis.service.GisService;
import com.example.bitserp.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gis")
@RequiredArgsConstructor
public class GisController {

    private final GisService gisService;

    @PostMapping("/locations")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','PROC_MANAGER')")
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(
            @Valid @RequestBody LocationRequest locationRequest
            ) {
        return ResponseEntity.ok(ApiResponse.ok(gisService.createLocation(locationRequest)));
    }

    @GetMapping("/locations")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','PROC_MANAGER','SALES_MANAGER')")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getLocations() {
        return ResponseEntity.ok(ApiResponse.ok(gisService.getAllLocations()));
    }

    @GetMapping("/locations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','PROC_MANAGER','SALES_MANAGER')")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocationById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(gisService.findById(id)));
    }

    @GetMapping("/locations/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','PROC_MANAGER','SALES_MANAGER')")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getLocationsByType(
            @PathVariable String type
    ) {
        return ResponseEntity.ok(ApiResponse.ok(gisService.getByType(type)));
    }

    @PostMapping("/locations/nearby")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','PROC_MANAGER','SALES_MANAGER')")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> findNearBy(
            @Valid @RequestBody NearbyRequest nearbyRequest
            ) {
        return ResponseEntity.ok(ApiResponse.ok(gisService.findNearBy(nearbyRequest)));
    }
}
