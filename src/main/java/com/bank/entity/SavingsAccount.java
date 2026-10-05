package com.bank.entity;

import com.bank.exception.InsufficientBalanceException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends BankAccount {

    public static final BigDecimal MINIMUM_BALANCE = new BigDecimal("500.00");

    public SavingsAccount() {}

    public SavingsAccount(String accountId, Customer customer, BigDecimal initialBalance) {
        super(accountId, customer, initialBalance);
        if (initialBalance.compareTo(MINIMUM_BALANCE) < 0) {
            throw new InsufficientBalanceException("Minimum initial balance of Rs 500 is required.");
        }
    }

    @Override
    public void credit(BigDecimal amount) {
        setBalance(getBalance().add(amount));
    }

    @Override
    public void debit(BigDecimal amount) {
        BigDecimal remaining = getBalance().subtract(amount);
        if (remaining.compareTo(MINIMUM_BALANCE) < 0) {
            throw new InsufficientBalanceException("Operation denied: Account balance cannot fall below Rs 500. Current balance: " + getBalance());
        }
        setBalance(remaining);
    }
}