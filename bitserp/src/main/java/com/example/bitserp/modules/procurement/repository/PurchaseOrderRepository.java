package com.example.bitserp.modules.procurement.repository;

import com.example.bitserp.modules.procurement.entity.PurchaseOrder;
import com.example.bitserp.modules.procurement.entity.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
    List<PurchaseOrder> findByVendorId(UUID vendorId);
    List<PurchaseOrder> findByRaisedById(UUID userId);
}
