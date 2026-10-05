package com.bank.entity;

import com.bank.exception.InsufficientBalanceException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CURRENT")
public class CurrentAccount extends BankAccount {

    private BigDecimal overdraftLimit;
    private BigDecimal overdraftUsed = BigDecimal.ZERO;

    public CurrentAccount() {}

    public CurrentAccount(String accountId, Customer customer, BigDecimal initialBalance, BigDecimal overdraftLimit) {
        super(accountId, customer, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void credit(BigDecimal amount) {
        if (overdraftUsed.compareTo(BigDecimal.ZERO) > 0) {
            if (amount.compareTo(overdraftUsed) >= 0) {
                BigDecimal remainingCredit = amount.subtract(overdraftUsed);
                overdraftUsed = BigDecimal.ZERO;
                setBalance(getBalance().add(remainingCredit));
            } else {
                overdraftUsed = overdraftUsed.subtract(amount);
            }
        } else {
            setBalance(getBalance().add(amount));
        }
    }

    @Override
    public void debit(BigDecimal amount) {
        BigDecimal availableFunds = getBalance().add(overdraftLimit.subtract(overdraftUsed));
        if (amount.compareTo(availableFunds) > 0) {
            throw new InsufficientBalanceException("Overdraft limit exceeded. Available credit: " + availableFunds);
        }

        if (amount.compareTo(getBalance()) <= 0) {
            setBalance(getBalance().subtract(amount));
        } else {
            BigDecimal excess = amount.subtract(getBalance());
            setBalance(BigDecimal.ZERO);
            overdraftUsed = overdraftUsed.add(excess);
        }
    }

    public BigDecimal getOverdraftLimit() { return overdraftLimit; }
    public BigDecimal getOverdraftUsed() { return overdraftUsed; }
}
