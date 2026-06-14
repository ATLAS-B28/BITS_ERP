package com.example.bitserp.modules.inventory.repository;

import com.example.bitserp.modules.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryAndActiveTrue(String category);
    boolean existsBySku(String sku);
}
