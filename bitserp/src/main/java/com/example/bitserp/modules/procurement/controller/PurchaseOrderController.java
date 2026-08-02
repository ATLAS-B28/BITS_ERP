package com.example.bitserp.modules.procurement.controller;

import com.example.bitserp.modules.procurement.dto.PurchaseOrderRequest;
import com.example.bitserp.modules.procurement.dto.PurchaseOrderResponse;
import com.example.bitserp.modules.procurement.entity.PurchaseOrderStatus;
import com.example.bitserp.modules.procurement.service.PurchaseOrderService;
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
@RequestMapping("/api/procurement")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER','PROC_EMPLOYEE')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPO(
            @Valid @RequestBody PurchaseOrderRequest request,
            @AuthenticationPrincipal String userEmail
            ) {
        return ResponseEntity.ok(ApiResponse.ok(
                purchaseOrderService.createPO(request, userEmail)
        ));
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER','PROC_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getAllPOs() {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.getAllPOs()));
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER','PROC_EMPLOYEE')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPO(
            @PathVariable UUID id
            ) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.getPOById(id)));
    }

    @GetMapping("/orders/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER','PROC_EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getPOsByStatus(
            @PathVariable PurchaseOrderStatus status
            ) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.getPOsByStatus(status)));
    }

    @PatchMapping("/orders/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER','PROC_EMPLOYEE')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> submitPO(
            @PathVariable UUID id
            ) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.submitPO(id)));
    }

    @PatchMapping("/orders/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER','PROC_EMPLOYEE')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> approvePO(
            @PathVariable UUID id,
            @AuthenticationPrincipal String approverEmail
            ) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.approvePO(id, approverEmail)));
    }

    @PatchMapping("/orders/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER','PROC_EMPLOYEE')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> rejectPO(
            @PathVariable UUID id
            ) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.rejectPO(id)));
    }

    @PatchMapping("/orders/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN','PROC_MANAGER','PROC_EMPLOYEE')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> receivePO(
            @PathVariable UUID id
            ) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.receivePO(id)));
    }
}
