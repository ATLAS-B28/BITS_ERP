package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.dto.BudgetRequest;
import com.example.bitserp.modules.finance.dto.BudgetResponse;
import com.example.bitserp.modules.finance.dto.BudgetStatusResponse;
import com.example.bitserp.modules.finance.entity.Budget;
import com.example.bitserp.modules.finance.entity.BudgetUtilization;
import com.example.bitserp.modules.finance.repository.BudgetRepository;
import com.example.bitserp.modules.finance.repository.BudgetUtilizationRepository;
import com.example.bitserp.shared.entity.User;
import com.example.bitserp.shared.exception.ResourceNotException;
import com.example.bitserp.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserRepository userRepository;

    public BudgetResponse createBudget(BudgetRequest budgetRequest, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new ResourceNotException("User Not Found"));
        Budget budget = new Budget();
        budget.setName(budgetRequest.getName());
        budget.setModule(budgetRequest.getModule());
        budget.setCategory(budgetRequest.getCategory());
        budget.setAllocatedAmount(budgetRequest.getAllocatedAmount());
        budget.setSpentAmount(BigDecimal.ZERO);
        if(!budgetRequest.getPeriodEnd().isAfter(budgetRequest.getPeriodStart())) {
            throw new ResourceNotException("Period End Date must be after Period Start Date");
        }
        budget.setPeriodStart(budgetRequest.getPeriodStart());
        budget.setPeriodEnd(budgetRequest.getPeriodEnd());
        budget.setStatus("DRAFT");
        budget.setCreatedBy(creator);
        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public BudgetResponse approveBudget(Integer id, String approverEmail) {
        Budget b = budgetRepository.findById(id).orElseThrow(() -> new ResourceNotException("Budget Not Found"));
        if(!"DRAFT".equals(b.getStatus())) {
            throw new IllegalStateException("Only DRAFT can be approved");
        }
        User approver = userRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new ResourceNotException("User Not Found"));

        boolean isAdmin = "ADMIN".equals(approver.getRole().getName());
        if(budgetRepository.existsOverlappingActive(
                b.getModule(), b.getPeriodStart(), b.getPeriodEnd()
        )) {
            throw new IllegalStateException("Budget Already Approved for Module: " + b.getModule() + " in this period" );
        }
        b.setStatus("ACTIVE");
        b.setApprovedBy(approver);
        return  toResponse(budgetRepository.save(b));
    }

    @Transactional
    public BudgetResponse closeBudget(Integer id) {      // also used to reject a draft
        Budget b = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotException("Budget not found"));
        if ("CLOSED".equals(b.getStatus())) {
            throw new IllegalStateException("Budget is already closed");
        }
        b.setStatus("CLOSED");
        return toResponse(budgetRepository.save(b));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets() {
        return budgetRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsByModule(String module) {
        return budgetRepository.findByModule(module)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public void validateAndDeductBudget(String module, BigDecimal amount, UUID referenceId) {
        Budget budget = budgetRepository
                .findActiveByModule(module, LocalDate.now())
                .orElse(null);

        if(budget == null) return;

        BigDecimal remaining = budget.getAllocatedAmount()
                .subtract(budget.getSpentAmount());

        if(amount.compareTo(remaining) > 0) {
            //budget.setStatus("EXCEEDED");
            //budgetRepository.save(budget);
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