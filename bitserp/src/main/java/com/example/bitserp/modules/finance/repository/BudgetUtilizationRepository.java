package com.example.bitserp.modules.finance.repository;

import com.example.bitserp.modules.finance.entity.BudgetUtilization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetUtilizationRepository extends JpaRepository<BudgetUtilization, Integer> {
    List<BudgetUtilization> findByBudgetId(Integer budgetId);
}
