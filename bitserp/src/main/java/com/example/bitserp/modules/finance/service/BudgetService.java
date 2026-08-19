package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.dto.BudgetRequest;
import com.example.bitserp.modules.finance.dto.BudgetResponse;
import com.example.bitserp.modules.finance.dto.BudgetStatusResponse;
import com.example.bitserp.modules.finance.entity.Budget;
import com.example.bitserp.modules.finance.entity.BudgetUtilization;
import com.example.bitserp.modules.finance.repository.BudgetRepository;
import com.example.bitserp.modules.finance.repository.BudgetUtilizationRepository;
import com.example.bitserp.shared.exception.ResourceNotException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<BudgetResponse> getAllBudgets() {
        return budgetRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<BudgetResponse> getBudgetsByModule(String module) {
        return budgetRepository.findByModule(module)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public BudgetStatusResponse getBudgetStatus(String module) {
        Budget budget = budgetRepository
                .findActiveByModule(module, LocalDate.now())
                .orElseThrow(() -> new ResourceNotException(
                        "Active budget for module " + module + " does not exist"
                ));

        BigDecimal remaining = budget.getAllocatedAmount()
                .subtract(budget.getSpentAmount());

        double utilizationPercent = budget.getSpentAmount()
                .divide(budget.getAllocatedAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        return new BudgetStatusResponse(
                module,
                budget.getAllocatedAmount(),
                budget.getSpentAmount(),
                remaining,
                utilizationPercent,
                budget.getStatus()
        );
    }

    public void validateAndDeductBudget(String module, BigDecimal amount, UUID referenceId) {
        Budget budget = budgetRepository
                .findActiveByModule(module, LocalDate.now())
                .orElse(null);

        if(budget == null) return;

        BigDecimal remaining = budget.getAllocatedAmount()
                .subtract(budget.getSpentAmount());

        if(amount.compareTo(remaining) > 0) {
            budget.setStatus("EXCEEDED");
            budgetRepository.save(budget);
            throw new IllegalArgumentException(
                    "PO amount exceeds remaining budget. Remaining: " + remaining
            );
        }

        budget.setSpentAmount(budget.getSpentAmount().add(amount));
        if(budget.getSpentAmount().compareTo(budget.getAllocatedAmount()) >= 0) {
            budget.setStatus("EXCEEDED");
        }
        budgetRepository.save(budget);

        BudgetUtilization util = new BudgetUtilization();
        util.setBudget(budget);
        util.setReferenceType("purchase_order");
        util.setReferenceId(referenceId);
        util.setAmount(amount);

        budgetUtilizationRepository.save(util);
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