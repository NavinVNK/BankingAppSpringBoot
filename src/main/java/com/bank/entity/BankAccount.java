package com.bank.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
public abstract class BankAccount {

    @Id
    private String accountId;

    @Embedded
    private Customer customer;

    private BigDecimal balance;

    public BankAccount() {}

    public BankAccount(String accountId, Customer customer, BigDecimal initialBalance) {
        this.accountId = accountId;
        this.customer = customer;
        this.balance = initialBalance;
    }

    public abstract void debit(BigDecimal amount);
    public abstract void credit(BigDecimal amount);

    // Getters and Setters
    public String getAccountId() { return accountId; }
    public Customer getCustomer() { return customer; }
    public BigDecimal getBalance() { return balance; }
    protected void setBalance(BigDecimal balance) { this.balance = balance; }
}