package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.dto.BudgetRequest;
import com.example.bitserp.modules.finance.dto.BudgetResponse;
import com.example.bitserp.modules.finance.entity.Budget;
import com.example.bitserp.modules.finance.repository.BudgetRepository;
import com.example.bitserp.modules.finance.repository.BudgetUtilizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetUtilizationRepository budgetUtilizationRepository;

    public BudgetResponse createBudget(BudgetRequest budgetRequest) {
        Budget budget = new Budget();
        budget.setName(budgetRequest.getName());
        budget.setModule(budgetRequest.getModule());
        budget.setCategory(budgetRequest.getCategory());
        budget.setAllocatedAmount(budgetRequest.getAllocatedAmount());
        budget.setSpentAmount(BigDecimal.ZERO);
        budget.setPeriodStart(budgetRequest.getPeriodStart());
        budget.setPeriodEnd(budgetRequest.getPeriodEnd());
        budget.setStatus("ACTIVE");
        return toResponse(budgetRepository.save(budget));
    }

    private BudgetResponse toResponse(Budget budget) {
        BigDecimal remaining = budget.getAllocatedAmount().subtract(budget.getSpentAmount());
        return new BudgetResponse(
                budget.getId(), budget.getName(), budget.getModule(),
                budget.getCategory(), budget.getAllocatedAmount(),
                budget.getSpentAmount(), remaining,
                budget.getPeriodStart(), budget.getPeriodEnd(), budget.getStatus()
        );
    }
}