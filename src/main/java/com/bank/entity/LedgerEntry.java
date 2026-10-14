package com.bank.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // CREDIT, DEBIT, ACCOUNT_CREATION, ACCOUNT_CLOSURE

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    public enum TransactionType {
        CREDIT, DEBIT, ACCOUNT_CREATION, ACCOUNT_CLOSURE, FIXED_DEPOSIT_DEBIT
    }

    public LedgerEntry() {}

    public LedgerEntry(String accountId, TransactionType type, BigDecimal amount, 
                       BigDecimal balanceBefore, BigDecimal balanceAfter, String description) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = Instant.now();
    }

    // Getters only (no setters to enforce immutability)
    public Long getId() { return id; }
    public String getAccountId() { return accountId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceBefore() { return balanceBefore; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public Instant getTimestamp() { return timestamp; }
}
