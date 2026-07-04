package com.example.bitserp.modules.inventory.repository;

import com.example.bitserp.modules.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByProductIdAndLocationId(UUID productId, Integer locationId);
    List<Inventory> findByLocationId(Integer locationId);
    List<Inventory> findByProductId(UUID productId);

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderLevel")
    List<Inventory> finLowStockItems();
}
