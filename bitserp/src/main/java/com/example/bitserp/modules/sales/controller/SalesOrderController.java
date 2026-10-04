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

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE','INV_MANAGER')")
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getDispatchedForMap() {
        return ResponseEntity.ok(ApiResponse.ok(
                salesOrderService.getDispatchedWithCoords()
        ));
    }

    @GetMapping("/orders/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getByStatus(
            @PathVariable SalesOrderStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                salesOrderService.getAllOrdersByStatus(status)
        ));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> getOrder(
            @PathVariable UUID id
            ) {
        return ResponseEntity.ok(ApiResponse.ok(salesOrderService.getOrder(id)));
    }


    @PatchMapping("/orders/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> confirmOrder(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(salesOrderService.confirmOrder(id)));
    }

    @PatchMapping("/orders/{id}/dispatch")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> dispatchOrder(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(salesOrderService.updateStatus(id, SalesOrderStatus.DISPATCHED)));
    }

    @PatchMapping("/orders/{id}/deliver")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> deliverOrder(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(salesOrderService.updateStatus(id, SalesOrderStatus.DELIVERED)));
    }

    @PatchMapping("/orders/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','SALES_EMPLOYEE')")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> cancelOrder(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(salesOrderService.cancelOrder(id)));
    }



}
