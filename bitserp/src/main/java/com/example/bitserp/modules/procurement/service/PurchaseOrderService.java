package com.example.bitserp.modules.procurement.service;

import com.example.bitserp.modules.inventory.repository.InventoryRepository;
import com.example.bitserp.modules.inventory.repository.ProductRepository;
import com.example.bitserp.modules.inventory.repository.StockMovementRepository;
import com.example.bitserp.modules.procurement.dto.PurchaseOrderItemResponse;
import com.example.bitserp.modules.procurement.dto.PurchaseOrderRequest;
import com.example.bitserp.modules.procurement.dto.PurchaseOrderResponse;
import com.example.bitserp.modules.procurement.entity.PurchaseOrder;
import com.example.bitserp.modules.procurement.entity.PurchaseOrderItem;
import com.example.bitserp.modules.procurement.entity.PurchaseOrderStatus;
import com.example.bitserp.modules.procurement.entity.Vendor;
import com.example.bitserp.modules.procurement.repository.PurchaseOrderRepository;
import com.example.bitserp.modules.procurement.repository.VendorRepository;
import com.example.bitserp.shared.entity.User;
import com.example.bitserp.shared.exception.ResourceNotException;
import com.example.bitserp.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorRepository vendorRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public PurchaseOrderResponse createPO(PurchaseOrderRequest purchaseOrderRequest, String userEmail) {
        Vendor vendor = vendorRepository.findById(purchaseOrderRequest.getVendorId())
                .orElseThrow(() -> new ResourceNotException("Vendor not found" + purchaseOrderRequest.getVendorId()));

        User raisedBy = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotException("User not found" + userEmail));

        PurchaseOrder po = new PurchaseOrder();
        po.setVendor(vendor);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setRaisedBy(raisedBy);

        List<PurchaseOrderItem> items = purchaseOrderRequest.getItems()
                .stream().map(itemReq -> {
                    var product = productRepository.findById(itemReq.getProductId())
                            .orElseThrow(() -> new ResourceNotException("Product not found" + itemReq.getProductId()));
                    PurchaseOrderItem item = new PurchaseOrderItem();
                    item.setPurchaseOrder(po);
                    item.setProduct(product);
                    item.setQuantity(item.getQuantity());
                    item.setUnitPrice(item.getUnitPrice());
                    return item;
                }).toList();

        po.setItems(items);

        BigDecimal total = items.stream()
                .map(
                        i -> i
                                .getUnitPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity()))
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
        po.setTotalAmount(total);

        return toResponse(purchaseOrderRepository.save(po));
    }

    private PurchaseOrderResponse toResponse(PurchaseOrder po) {
        List<PurchaseOrderItemResponse> itemResponses = po.getItems().stream()
                .map(i -> new PurchaseOrderItemResponse(
                        i.getId(),
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotalPrice()
                ))
                .toList();

        return new PurchaseOrderResponse(
                po.getId(),
                po.getVendor().getName(),
                po.getStatus(),
                po.getTotalAmount(),
                po.getRaisedBy() != null ? po.getRaisedBy().getEmail() : null,
               // po.getApprovedBy() != null ? po.getApprovedBy().getEmail() : null,
                itemResponses,
                po.getCreatedAt()
        );
    }
}
