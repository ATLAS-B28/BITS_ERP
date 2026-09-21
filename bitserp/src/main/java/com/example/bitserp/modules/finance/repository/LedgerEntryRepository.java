package com.example.bitserp.modules.finance.repository;

import com.example.bitserp.modules.finance.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Integer> {
    List<LedgerEntry> findByType(String type);
    List<LedgerEntry> findByReferenceTypeAndReferenceId(String referenceType, UUID referenceId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM LedgerEntry e" +
            " WHERE e.type = 'DEBIT'")
    BigDecimal findTotalDebits();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM LedgerEntry e WHERE " +
            "e.type = 'CREDIT'")
    BigDecimal findTotalCredits();

}
