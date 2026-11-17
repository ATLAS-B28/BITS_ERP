package com.example.bitserp.modules.procurement.service;

import com.example.bitserp.modules.finance.service.CurrencyService;
import com.example.bitserp.modules.finance.service.LedgerService;
import com.example.bitserp.modules.finance.service.TaxService;
import com.example.bitserp.modules.inventory.entity.Inventory;
import com.example.bitserp.modules.inventory.entity.StockMovement;
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
import java.util.UUID;
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
    private final LedgerService ledgerService;
    private final TaxService taxService;
    private final CurrencyService currencyService;

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
        po.setCurrency(purchaseOrderRequest.getCurrency() != null
        ? purchaseOrderRequest.getCurrency() :  "INR");
        po.setTdsCategory(purchaseOrderRequest.getTdsCategory() != null
        ? purchaseOrderRequest.getTdsCategory() : "goods");


        List<PurchaseOrderItem> items = purchaseOrderRequest.getItems()
                .stream().map(itemReq -> {
                    var product = productRepository
                            .findById(itemReq.getProductId())
                            .orElseThrow(() -> new ResourceNotException("Product not found" + itemReq.getProductId()));
                    PurchaseOrderItem item = new PurchaseOrderItem();
                    item.setPurchaseOrder(po);
                    item.setProduct(product);
                    item.setQuantity(itemReq.getQuantity());
                    item.setUnitPrice(itemReq.getUnitPrice());
//                    item.setTotalPrice(itemReq.getUnitPrice()
//                            .multiply(BigDecimal.valueOf(item.getQuantity())));
                    return item;
                }).toList();

        po.setItems(items);

//        BigDecimal subtotalInCurrency = items.stream()
//                .map(
//                        i -> i
//                                .getUnitPrice()
//                                .multiply(BigDecimal.valueOf(i.getQuantity()))
//                )
//                .reduce(
//                        BigDecimal.ZERO,
//                        BigDecimal::add
//                );
//
//        BigDecimal subtotalINR = currencyService.toINR(
//                subtotalInCurrency, po.getCurrency()
//        );
//
//        BigDecimal exchangeRate = currencyService.getRate(
//                po.getCurrency(), "INR"
//        );

//        String vendorState = vendor.getLocation() != null
//                ? vendor.getLocation().getState() : null;
//        boolean isInterstate = taxService.isInterstate(vendorState);
//        po.setVendorState(vendorState);
//        po.setIsInterstate(isInterstate);
//
//        BigDecimal gstRate = items.isEmpty()
//                ? BigDecimal.valueOf(18) :
//                items.getFirst().getProduct().getGstRate() != null
//                ? items.getFirst().getProduct().getGstRate()
//                        : BigDecimal.valueOf(18);
//        TaxService.TaxCalculation tax = taxService.calculatePOTax(
//                subtotalINR, gstRate,
//                po.getTdsCategory(), vendorState, isInterstate
//        );
//        po.setSubtotal(subtotalINR);
//        po.setTotalAmount(subtotalINR);
//        po.setCgstAmount(tax.getCgstAmount());
//        po.setIgstAmount(tax.getIgstAmount());
//        po.setSgstAmount(tax.getSgstAmount());
//        po.setTdsAmount(tax.getTdsAmount());
//        po.setTaxTotal(tax.getTaxTotal());
//        po.setGrandTotal(tax.getGrandTotal());
//
//        po.setExchangeRate(exchangeRate);
        applyTaxes(po, items);

        return toResponse(purchaseOrderRepository.save(po));
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getAllPOs() {
        return purchaseOrderRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPOById(UUID id) {
        return toResponse(purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Purchase order not found" + id)));
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getPOsByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PurchaseOrderResponse submitPO(UUID id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Purchase Order not found" + id));
        if(po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT purchase orders can be created");
        }

        po.setStatus(PurchaseOrderStatus.SUBMITTED);
        return toResponse(purchaseOrderRepository.save(po));
    }

    @Transactional
    public PurchaseOrderResponse approvePO(UUID id, String approverEmail) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Purchase Order not found" + id));
        if(po.getStatus() != PurchaseOrderStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED purchase orders can be approved");
        }
        User approver = userRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new ResourceNotException("User not found" + approverEmail));

        po.setStatus(PurchaseOrderStatus.APPROVED);
        po.setApprovedBy(approver);

        ledgerService.recordDebit(
                po.getTotalAmount(),
                "purchase_order",
                po.getId(),
                "PO approved" + po.getId()
        );

        return toResponse(purchaseOrderRepository.save(po));
    }

    @Transactional
    public PurchaseOrderResponse rejectPO(UUID id, String reason) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Purchase Order not found" + id));
        if(po.getStatus() != PurchaseOrderStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED purchase orders can be rejected");
        }

        po.setStatus(PurchaseOrderStatus.REJECTED);
        po.setCancellationReason(reason);
        return toResponse(purchaseOrderRepository.save(po));
    }

    @Transactional
    public void applyTaxes(PurchaseOrder po, List<PurchaseOrderItem> items) {
        String currency = po.getCurrency() != null ? po.getCurrency() : "INR";

        BigDecimal entered = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotalInr = currencyService.toINR(entered, currency);
        po.setExchangeRate(currencyService.getRate(currency, "INR"));

        String vendorState = po.getVendor().getLocation() != null
                ? po.getVendor().getLocation().getState() : null;
        boolean interstate = taxService.isInterstate(vendorState);
        po.setVendorState(vendorState);
        po.setIsInterstate(interstate);

        BigDecimal gstRate = items.isEmpty() || items.getFirst().getProduct().getGstRate() == null
                ? BigDecimal.valueOf(18)
                : items.getFirst().getProduct().getGstRate();

        String tdsCategory = po.getTdsCategory() != null ? po.getTdsCategory() : "goods";

        TaxService.TaxCalculation tax = taxService.calculatePOTax(
                subtotalInr, gstRate, tdsCategory, vendorState, interstate
        );

        po.setSubtotal(subtotalInr);
        po.setTotalAmount(subtotalInr);
        po.setCgstAmount(tax.getCgstAmount());
        po.setSgstAmount(tax.getSgstAmount());
        po.setIgstAmount(tax.getIgstAmount());
        po.setTdsAmount(tax.getTdsAmount());
        po.setTaxTotal(tax.getTaxTotal());
        po.setGrandTotal(tax.getGrandTotal());
    }

    @Transactional
    public PurchaseOrderResponse receivePO(UUID id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Purchase Order not found" + id));
        if(po.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED purchase orders can be received");
        }

        po.getItems().forEach(item -> {
            List<Inventory> inventoryList = inventoryRepository.findByProductId(item.getProduct().getId());

            if(!inventoryList.isEmpty()) {
                Inventory inv = inventoryList.getFirst();
                inv.setQuantity(inv.getQuantity() + item.getQuantity());
                inventoryRepository.save(inv);
                StockMovement movement = new StockMovement();
                movement.setProduct(item.getProduct());
                movement.setLocation(inv.getLocation());
                movement.setChangeQty(item.getQuantity());
                movement.setReason("purchase_order");
                movement.setReferenceId(po.getId());

                stockMovementRepository.save(movement);
            }

        });

        po.setStatus(PurchaseOrderStatus.RECEIVED);

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
                po.getId(),                    // UUID id
                po.getVendor().getName(),      // String vendorName
                po.getStatus(),                // PurchaseOrderStatus
                po.getSubtotal(),              // BigDecimal subtotal
                po.getTotalAmount(),           // BigDecimal totalAmount
                po.getGrandTotal(),            // BigDecimal grandTotal
                po.getCgstAmount(),            // BigDecimal cgstAmount
                po.getSgstAmount(),            // BigDecimal sgstAmount
                po.getIgstAmount(),            // BigDecimal igstAmount
                po.getTdsAmount(),             // BigDecimal tdsAmount
                po.getTaxTotal(),              // BigDecimal taxTotal
                po.getCurrency(),              // String currency
                po.getExchangeRate(),          // BigDecimal exchangeRate
                po.getIsInterstate(),          // Boolean isInterstate
                po.getTdsCategory(),           // String tdsCategory
                po.getCancellationReason(),    // String cancellationReason
                po.getRaisedBy() != null
                        ? po.getRaisedBy().getEmail() : null,    // String raisedBy
                po.getApprovedBy() != null
                        ? po.getApprovedBy().getEmail() : null,  // String approvedBy
                itemResponses,                 // List<PurchaseOrderItemResponse>
                po.getCreatedAt()              // OffsetDateTime createdAt

                );
    }
}
