package com.example.bitserp.modules.sales.controller;

import com.example.bitserp.modules.sales.dto.SalesOrderRequest;
import com.example.bitserp.modules.sales.dto.SalesOrderResponse;
import com.example.bitserp.modules.sales.entity.SalesOrderStatus;
import com.example.bitserp.modules.sales.service.SalesOrderService;
import com.example.bitserp.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> createOrder(
            @Valid @RequestBody SalesOrderRequest request,
            @AuthenticationPrincipal String userEmail
            ) {
        return ResponseEntity.ok(ApiResponse.ok(salesOrderService.createSaleOrder(request, userEmail)));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.ok(salesOrderService.getAllOrders()));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> getOrder(
            @PathVariable UUID id
            ) {
        return ResponseEntity.ok(ApiResponse.ok(salesOrderService.getOrder(id)));
    }

    @GetMapping("/orders/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getByStatus(
            @PathVariable SalesOrderStatus status
            ) {
        return ResponseEntity.ok(ApiResponse.ok(
                salesOrderService.getAllOrdersByStatus(status)
        ));
    }
}
