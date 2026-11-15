package com.example.bitserp.modules.automation.service;

import com.example.bitserp.modules.inventory.entity.Inventory;
import com.example.bitserp.modules.procurement.entity.PurchaseOrder;
import com.example.bitserp.modules.procurement.entity.PurchaseOrderItem;
import com.example.bitserp.modules.procurement.entity.PurchaseOrderStatus;
import com.example.bitserp.modules.procurement.repository.PurchaseOrderRepository;
import com.example.bitserp.modules.procurement.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutomationService {

    private final AutomationSettingsService settingsService;
    private final NotificationService notificationService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorRepository vendorRepository;

    @Transactional
    public void checkAndTriggerAutoPO(Inventory inventory) {
        if(!settingsService.getBoolean("auto_po_on_low_stock")) {
            return;
        }
        if(inventory.getQuantity() > inventory.getReorderLevel()) {
            return;
        }
        String vendorIdStr = settingsService.get("auto_po_default_vendor_id");
        if(vendorIdStr == null || vendorIdStr.isBlank()) {
            log.warn("Auto PO triggered but no default vendor set");
            notifyManagers(
                    "⚠ Auto PO Failed",
                    "Low stock detected for " + inventory.getProduct().getName()
                            + " but no default vendor is configured. "
                            + "Please create a PO manually.",
                    "WARNING", null
            );
            return;
        }

        boolean pendingExists = purchaseOrderRepository
                .findAll().stream()
                .anyMatch(po ->
                        (po.getStatus() == PurchaseOrderStatus.DRAFT
                        || po.getStatus() == PurchaseOrderStatus.SUBMITTED) &&
                        po.getItems().stream().anyMatch(item ->
                                item.getProduct().getId()
                                        .equals(inventory.getProduct().getId())));
        if(pendingExists) {
            log.info("Auto PO triggered but no default vendor set");
        }

        try {
            UUID vendorId = UUID.fromString(vendorIdStr);
            var vendor = vendorRepository.findById(vendorId).orElse(null);
            if(vendor == null) {
                log.warn("Auto PO triggered but no default vendor with id {}", vendorId);
                return;
            }
            int multiplier = settingsService.getInt(
                    "auto_po_quantity_multiplier", 2
            );
            int orderQty = inventory.getReorderLevel() * multiplier;
            PurchaseOrder po = new PurchaseOrder();
            po.setVendor(vendor);
            po.setStatus(PurchaseOrderStatus.DRAFT);
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(po);
            item.setProduct(inventory.getProduct());
            item.setQuantity(orderQty);
            item.setUnitPrice(inventory.getProduct().getUnitPrice());
            item.setTotalPrice(
                    inventory.getProduct().getUnitPrice()
                            .multiply(BigDecimal.valueOf(orderQty))
            );
            po.setItems(List.of(item));
            po.setTotalAmount(item.getTotalPrice());

            PurchaseOrder saved = purchaseOrderRepository.save(po);

            log.info("Auto PO: {} for product {}",
                    saved.getId(), inventory.getProduct().getName());

            notifyManagers(
                    "🤖 Auto PO Created",
                    "Low stock alert: " + inventory.getProduct().getName()
                            + " (" + inventory.getQuantity() + " units remaining). "
                            + "A draft PO has been created for "
                            + vendor.getName() + " — "
                            + orderQty + " units. Please review and approve.",
                    "WARNING",
                    saved.getId()
            );
        } catch (Exception e) {
            log.error("Auto PO triggered but no default vendor set", e);
        }
    }

    private void notifyManagers(String title, String message,
                                String type, UUID referenceId) {
        String rolesStr = settingsService.get("auto_po_notify_roles");
        List<String> roles = Arrays.asList(
                rolesStr.split(","));
        notificationService.notifyByRoles(
                roles, title, message, type,
                "purchase_order", referenceId
        );
    }
}
