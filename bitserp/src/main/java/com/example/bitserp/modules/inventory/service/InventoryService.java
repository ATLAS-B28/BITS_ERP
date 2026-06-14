package com.example.bitserp.modules.inventory.service;

import com.example.bitserp.modules.inventory.dto.ProductRequest;
import com.example.bitserp.modules.inventory.dto.ProductResponse;
import com.example.bitserp.modules.inventory.dto.StockUpdateRequest;
import com.example.bitserp.modules.inventory.entity.Inventory;
import com.example.bitserp.modules.inventory.entity.Product;
import com.example.bitserp.modules.inventory.entity.StockMovement;
import com.example.bitserp.modules.inventory.repository.InventoryRepository;
import com.example.bitserp.modules.inventory.repository.ProductRepository;
import com.example.bitserp.modules.inventory.repository.StockMovementRepository;
import com.example.bitserp.shared.entity.Location;
import com.example.bitserp.shared.exception.ResourceNotException;
import com.example.bitserp.shared.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final LocationRepository locationRepository;

    public ProductResponse createProduct(ProductRequest request) {
        if(productRepository.existsBySku(request.getSku())) {
            throw  new IllegalArgumentException("sku already exists: " + request.getSku());
        }
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setUnitPrice(request.getUnitPrice());
        product.setUnitOfMeasure(request.getUnitOfMeasure());
        product.setDescription(request.getDescription());

        return toResponse(productRepository.save(product));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProduct(UUID id) {
        return toResponse(productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotException("Product not found: " + id))
        );
    }

    @Transactional
    public void updateStock(UUID productId, StockUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotException("Product not found: " + productId));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotException("Location not found: " + request.getLocationId()));
        Inventory inventory = inventoryRepository.findByProductIdAndLocationId(productId, request.getLocationId())
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setProduct(product);
                    inv.setLocation(location);
                    inv.setQuantity(0);
                    return inv;
                });
        int newQty = inventory.getQuantity() + request.getChangeQty();
        if(newQty < 0) throw new IllegalArgumentException("Quantity cannot be less than 0");
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);

        StockMovement move = new StockMovement();
        move.setProduct(product);
        move.setLocation(location);
        move.setChangeQty(request.getChangeQty());
        stockMovementRepository.save(move);
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.finLowStockItems();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(), product.getSku(),
                product.getName(), product.getCategory(),
                product.getUnitPrice(), product.getUnitOfMeasure(),
                product.getActive()
        );
    }
}
