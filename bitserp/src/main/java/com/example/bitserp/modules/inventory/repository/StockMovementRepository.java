package com.example.bitserp.modules.inventory.repository;

import com.example.bitserp.modules.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {
    List<StockMovement> findByProductIdOrderByMovedAtDesc(UUID productId);
    List<StockMovement> findByLocationIdOrderByMovedAtDesc(Integer locationId);
    List<StockMovement> findByReason(String reason);
}
