package com.bank.repository;

import com.bank.entity.LedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {
    
    // Retrieve paginated ledger statement for a specific account sorted chronologically
    Page<LedgerEntry> findByAccountIdOrderByTimestampDesc(String accountId, Pageable pageable);
}
