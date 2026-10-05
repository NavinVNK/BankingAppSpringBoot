package com.bank.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Entity
@Table(name = "fixed_deposits")
public class FixedDeposit {

    @Id
    private String depositId;

    // Many Fixed Deposits can belong to one BankAccount
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount account;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer durationYears;

    @Column(nullable = false)
    private Instant createdAt;

    public FixedDeposit() {}

    public FixedDeposit(String depositId, BankAccount account, BigDecimal principalAmount, BigDecimal interestRate, Integer durationYears) {
        this.depositId = depositId;
        this.account = account;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.durationYears = durationYears;
        this.createdAt = Instant.now();
    }

    /**
     * Calculates simple interest: (Principal * Rate * Years) / 100
     */
    public BigDecimal calculateInterest() {
        return principalAmount
                .multiply(interestRate)
                .multiply(BigDecimal.valueOf(durationYears))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates total maturity amount: Principal + Computed Interest
     */
    public BigDecimal getMaturityAmount() {
        return principalAmount.add(calculateInterest());
    }

    // Getters and Setters
    public String getDepositId() { return depositId; }
    public void setDepositId(String depositId) { this.depositId = depositId; }

    public BankAccount getAccount() { return account; }
    public void setAccount(BankAccount account) { this.account = account; }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public Integer getDurationYears() { return durationYears; }
    public void setDurationYears(Integer durationYears) { this.durationYears = durationYears; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}