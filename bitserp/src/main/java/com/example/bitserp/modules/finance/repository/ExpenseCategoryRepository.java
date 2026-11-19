package com.example.bitserp.modules.finance.repository;

import com.example.bitserp.modules.finance.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Integer> {
    List<ExpenseCategory> findByModuleAndActiveTrue(String module);
    List<ExpenseCategory> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
