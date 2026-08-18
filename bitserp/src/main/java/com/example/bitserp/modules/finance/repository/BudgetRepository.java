package com.example.bitserp.modules.finance.repository;

import com.example.bitserp.modules.finance.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findByModule(String module);
    List<Budget> findByStatus(String status);

    @Query("SELECT b FROM Budget b WHERE b.module = :module " +
            "AND b.status = 'ACTIVE' " +
            "AND b.periodStart <= :date AND b.periodEnd >= :date")
    Optional<Budget> findActiveByModule(
            @Param("module") String module,
            @Param("date") LocalDate date
            );
}
