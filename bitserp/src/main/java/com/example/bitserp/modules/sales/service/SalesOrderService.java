package com.example.bitserp.modules.sales.service;

import com.example.bitserp.modules.finance.service.CurrencyService;
import com.example.bitserp.modules.finance.service.LedgerService;
import com.example.bitserp.modules.finance.service.TaxService;
import com.example.bitserp.modules.inventory.entity.Inventory;
import com.example.bitserp.modules.inventory.entity.StockMovement;
import com.example.bitserp.modules.inventory.repository.InventoryRepository;
import com.example.bitserp.modules.inventory.repository.ProductRepository;
import com.example.bitserp.modules.inventory.repository.StockMovementRepository;
import com.example.bitserp.modules.sales.dto.SalesOrderItemResponse;
import com.example.bitserp.modules.sales.dto.SalesOrderRequest;
import com.example.bitserp.modules.sales.dto.SalesOrderResponse;
import com.example.bitserp.modules.sales.entity.Customer;
import com.example.bitserp.modules.sales.entity.SalesOrder;
import com.example.bitserp.modules.sales.entity.SalesOrderItem;
import com.example.bitserp.modules.sales.entity.SalesOrderStatus;
import com.example.bitserp.modules.sales.repository.CustomerRepository;
import com.example.bitserp.modules.sales.repository.SalesOrderRepository;
import com.example.bitserp.shared.entity.User;
import com.example.bitserp.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final LedgerService ledgerService;
    private final TaxService taxService;
    private final CurrencyService currencyService;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public SalesOrderResponse createSaleOrder(SalesOrderRequest request, String userEmail) {
        System.out.println("Items count: " + request.getItems().size());
        System.out.println("First item: " + request.getItems().get(0).getProductId());
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        User createdBy = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SalesOrder order = new SalesOrder();
        order.setCustomer(customer);
        order.setStatus(SalesOrderStatus.PENDING);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setCreatedBy(createdBy);
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");

        String customerState = customer.getLocation() != null
                ? customer.getLocation().getState() : null;
        String customerCountry = request.getCustomerCountry() != null
                ? request.getCustomerCountry() : "IN";
        boolean isExport = !"IN".equalsIgnoreCase(customerCountry);
        boolean isInterState = !isExport && taxService.isInterstate(customerState);

        order.setCustomerState(customerState);
        order.setCustomerCountry(customerCountry);
        order.setIsExport(isExport);
        order.setIsInterstate(isInterState);

        if(request.getDeliveryLatitude() != null && request.getDeliveryLongitude() != null) {
            order.setDeliveryCords(
                    new org.locationtech.jts.geom.GeometryFactory(
                            new org.locationtech.jts.geom.PrecisionModel(), 4326)
                            .createPoint(new org.locationtech.jts.geom.Coordinate(
                                    request.getDeliveryLongitude(),
                                    request.getDeliveryLatitude()))
            );
        }

        List<SalesOrderItem> items = request.getItems().stream().map(itemReq -> {
            var product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found" +  itemReq.getProductId()));

            List<Inventory> inventories = inventoryRepository.findByProductId(product.getId());
            int totalStock = inventories.stream().mapToInt(Inventory::getQuantity).sum();
            if(totalStock < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock" + product.getName());
            }
            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrder(order);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setProduct(product);
            return item;
        }).toList();

        order.setItems(items);

        BigDecimal subtotalInCurrency = items.stream()
                .map(i -> i.getUnitPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotalInr = currencyService.toINR(
                subtotalInCurrency, order.getCurrency()
        );

        BigDecimal exchangeRate = currencyService.getRate(
                order.getCurrency(), "INR"
        );
        order.setExchangeRate(exchangeRate);

        BigDecimal gstRate = items.isEmpty()
                ? BigDecimal.valueOf(18)
                : items.getFirst().getProduct().getGstRate() != null
                ? items.getFirst().getProduct().getGstRate()
                : BigDecimal.valueOf(18);

        TaxService.TaxCalculation tax = taxService.calculateSOTax(
                subtotalInr, gstRate,
                customerState, isInterState, isExport
        );

        order.setSubtotal(subtotalInr);
        order.setTotalAmount(subtotalInr);
        order.setCgstAmount(tax.getCgstAmount());
        order.setSgstAmount(tax.getSgstAmount());
        order.setIgstAmount(tax.getIgstAmount());
        order.setTaxTotal(tax.getTaxTotal());
        order.setGrandTotal(tax.getGrandTotal());


        return toResponse(salesOrderRepository.save(order));
    }

    @Transactional
    public SalesOrderResponse confirmOrder(UUID id) {
        SalesOrder order = getOrderEntity(id);
        if(order.getStatus() != SalesOrderStatus.PENDING) {
            throw new IllegalArgumentException("Order is not pending");
        }
        order.getItems().forEach(item -> {
            List<Inventory> inventories = inventoryRepository.findByProductId(item.getProduct().getId());
            if(!inventories.isEmpty()) {
                Inventory inv = inventories.getFirst();
                inv.setQuantity(inv.getQuantity() - item.getQuantity());
                inventoryRepository.save(inv);

                StockMovement stockMovement = new StockMovement();
                stockMovement.setProduct(item.getProduct());
                stockMovement.setLocation(inv.getLocation());
                stockMovement.setChangeQty(inv.getQuantity());
                stockMovement.setReason("sale");
                stockMovement.setReferenceId(order.getId());
                stockMovementRepository.save(stockMovement);
            }
        });
        order.setStatus(SalesOrderStatus.CONFIRMED);

        ledgerService.recordCredit(
                order.getTotalAmount(),
                "sales_order",
                order.getId(),
                "Sales order confirmed: " + order.getId()
        );

        return toResponse(salesOrderRepository.save(order));
    }

    @Transactional
    public SalesOrderResponse updateStatus(UUID id, SalesOrderStatus newStatus) {
        SalesOrder order = getOrderEntity(id);
        order.setStatus(newStatus);
        return toResponse(salesOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getAllOrders() {
        return salesOrderRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalesOrderResponse getOrder(UUID id) {
        return toResponse(salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found")));
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getAllOrdersByCustomerId(UUID customerId) {
        return salesOrderRepository.findByCustomerId(customerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getAllOrdersByStatus(SalesOrderStatus status) {
        return salesOrderRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public SalesOrderResponse cancelOrder(UUID id, String reason) {
        SalesOrder order = getOrderEntity(id);
        if(order.getStatus() == SalesOrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Order is not delivered");
        }
        if(order.getStatus() == SalesOrderStatus.CONFIRMED||
             order.getStatus() == SalesOrderStatus.DISPATCHED) {
                order.getItems().forEach(item -> {
                    List<Inventory> inventories = inventoryRepository.findByProductId(item.getProduct().getId());
                    if(!inventories.isEmpty()) {
                        Inventory inv = inventories.getFirst();
                        inv.setQuantity(inv.getQuantity() + item.getQuantity());
                        inventoryRepository.save(inv);

                        StockMovement stockMovement = new StockMovement();
                        stockMovement.setProduct(item.getProduct());
                        stockMovement.setLocation(inv.getLocation());
                        stockMovement.setChangeQty(item.getQuantity());
                        stockMovement.setReason("return");
                        stockMovement.setReferenceId(order.getId());
                        stockMovementRepository.save(stockMovement);
                    }
                });
        }
        order.setStatus(SalesOrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        return toResponse(salesOrderRepository.save(order));
    }

    private SalesOrder getOrderEntity(UUID id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    private SalesOrderResponse toResponse(SalesOrder salesOrder) {
        List<SalesOrderItemResponse> itemResponses = salesOrder.getItems()
                .stream()
                .map(i -> new SalesOrderItemResponse(
                        i.getId(),
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotalPrice()
                )).toList();

        Double lat = salesOrder.getDeliveryCords() != null
                ? salesOrder.getDeliveryCords().getY() : null;
        Double lng = salesOrder.getDeliveryCords() != null
                ? salesOrder.getDeliveryCords().getX() : null;

        return new SalesOrderResponse(
                salesOrder.getId(),
                salesOrder.getCustomer().getName(),
                salesOrder.getStatus(),
                salesOrder.getDeliveryAddress(),
                lat,
                lng,
                salesOrder.getTotalAmount(),
                salesOrder.getCreatedBy() != null ? salesOrder.getCreatedBy().getEmail() : null,
                itemResponses,
                salesOrder.getCreatedAt(),
                salesOrder.getSubtotal(),
                salesOrder.getGrandTotal(),
                salesOrder.getCgstAmount(),
                salesOrder.getSgstAmount(),
                salesOrder.getIgstAmount(),
                salesOrder.getTaxTotal(),
                salesOrder.getCurrency(),
                salesOrder.getExchangeRate(),
                salesOrder.getIsInterstate(),
                salesOrder.getIsExport(),
                salesOrder.getCustomerCountry(),
                salesOrder.getCancellationReason()
        );
    }

    @Transactional(readOnly = true)
    public List<SalesOrderResponse> getDispatchedWithCoords() {
        return salesOrderRepository.findDispatchedWithCoords()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
}
