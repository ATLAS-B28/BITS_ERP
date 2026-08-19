package com.example.bitserp.modules.finance.service;

import com.example.bitserp.modules.finance.dto.FinanceSummaryResponse;
import com.example.bitserp.modules.finance.dto.LedgerEntryResponse;
import com.example.bitserp.modules.finance.entity.Budget;
import com.example.bitserp.modules.finance.entity.LedgerEntry;
import com.example.bitserp.modules.finance.repository.BudgetRepository;
import com.example.bitserp.modules.finance.repository.LedgerEntryRepository;
import com.example.bitserp.modules.sales.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final BudgetRepository budgetRepository;
    private final SalesOrderRepository salesOrderRepository;

    public void recordDebit(BigDecimal amount, String referenceType, UUID referenceId, String description) {
        LedgerEntry entry = new LedgerEntry();
        entry.setType("DEBIT");
        entry.setAmount(amount);
        entry.setReferenceType(referenceType);
        entry.setReferenceId(referenceId);
        entry.setDescription(description);
        ledgerEntryRepository.save(entry);
    }

    public void recordCredit(BigDecimal amount, String referenceType, UUID referenceId, String description) {
        LedgerEntry entry = new LedgerEntry();
        entry.setType("CREDIT");
        entry.setAmount(amount);
        entry.setReferenceType(referenceType);
        entry.setReferenceId(referenceId);
        entry.setDescription(description);
        ledgerEntryRepository.save(entry);
    }

    public List<LedgerEntryResponse> getAllEntries() {
        return ledgerEntryRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<LedgerEntryResponse> getByType(String type) {
        return ledgerEntryRepository.findByType(type)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public FinanceSummaryResponse getSummary() {
        BigDecimal totalDebits = ledgerEntryRepository.findTotalDebits();
        BigDecimal totalCredits = ledgerEntryRepository.findTotalCredits();
        BigDecimal netBalance = totalCredits.subtract(totalDebits);
        BigDecimal totalRevenue = salesOrderRepository.getTotalAmount();
        BigDecimal totalAllocated = budgetRepository.findAll().stream()
                .map(Budget::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSpent = budgetRepository.findAll().stream()
                .map(Budget::getSpentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinanceSummaryResponse(
                totalDebits, totalCredits, netBalance,
                totalRevenue, totalAllocated, totalSpent
        );
    }

    private LedgerEntryResponse toResponse(LedgerEntry entry) {
        return new LedgerEntryResponse(
                entry.getId(), entry.getType(), entry.getAmount(),
                entry.getDescription(), entry.getReferenceType(),
                entry.getReferenceId(), entry.getCreatedAt()
        );
    }
}
