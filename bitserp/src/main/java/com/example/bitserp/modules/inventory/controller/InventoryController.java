package com.example.bitserp.modules.inventory.controller;

import com.example.bitserp.modules.inventory.dto.ProductRequest;
import com.example.bitserp.modules.inventory.dto.ProductResponse;
import com.example.bitserp.modules.inventory.dto.StockUpdateRequest;
import com.example.bitserp.modules.inventory.entity.Inventory;
import com.example.bitserp.modules.inventory.service.InventoryService;
import com.example.bitserp.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/products")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request
            ) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.createProduct(request)));
    }

    @GetMapping("/products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','INV_EMPLOYEE'")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getProduct(id)));
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','INV_EMPLOYEE'")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllProducts()));
    }

    @PostMapping("/products/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER','INV_EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            @PathVariable UUID id, @Valid @RequestBody StockUpdateRequest request
            ) {
        inventoryService.updateStock(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Stock updated",null));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','INV_MANAGER')")
    public ResponseEntity<ApiResponse<List<Inventory>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStockItems()));
    }

}
