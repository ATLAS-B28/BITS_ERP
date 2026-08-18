package com.example.bitserp.modules.finance.repository;

import com.example.bitserp.modules.finance.entity.RevenueProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RevenueProjectionRepository extends JpaRepository<RevenueProjection, Integer> {
    List<RevenueProjection> findByRegion(String region);
    Optional<RevenueProjection> findByProjectionDateAndRegion(LocalDate date, String region);
    List<RevenueProjection> findByProjectionDateAfter(LocalDate date);
}
