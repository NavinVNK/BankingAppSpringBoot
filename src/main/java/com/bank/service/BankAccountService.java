package com.bank.service;

import com.bank.dto.AccountOperationRequest;
import com.bank.dto.CreateAccountRequest;
import com.bank.dto.FixedDepositRequest;
import com.bank.entity.Address;
import com.bank.entity.BankAccount;
import com.bank.entity.CurrentAccount;
import com.bank.entity.LedgerEntry.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.bank.entity.Customer;
import com.bank.entity.FixedDeposit;
import com.bank.entity.LedgerEntry;
import com.bank.entity.SavingsAccount;
import com.bank.exception.MaxAccountLimitReachedException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.repository.LedgerRepository;
import com.bank.repository.BankAccountRepository;
import com.bank.repository.FixedDepositRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BankAccountService {

    private static final long MAX_ACCOUNTS = 100;
    private final BankAccountRepository accountRepository;
    private final FixedDepositRepository fdRepository;
    private final LedgerRepository ledgerRepository;

    public BankAccountService(BankAccountRepository accountRepository, FixedDepositRepository fdRepository,LedgerRepository ledgerRepository) {
        this.accountRepository = accountRepository;
        this.fdRepository = fdRepository;
        this.ledgerRepository = ledgerRepository;
    }

    public BankAccount createAccount(CreateAccountRequest req) {
        if (accountRepository.count() >= MAX_ACCOUNTS) {
            throw new MaxAccountLimitReachedException("Bank capacity reached. At most " + MAX_ACCOUNTS + " accounts can be created.");
        }

        String accountId = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Address address = new Address(req.doorNumber(), req.streetName(), req.city(), req.pinCode());
        Customer customer = new Customer(req.name(), address);

        BankAccount account;
        if ("SAVINGS".equalsIgnoreCase(req.accountType())) {
            account = new SavingsAccount(accountId, customer, req.initialBalance());
        } else if ("CURRENT".equalsIgnoreCase(req.accountType())) {
            BigDecimal overdraft = req.overdraftLimit() != null ? req.overdraftLimit() : BigDecimal.ZERO;
            account = new CurrentAccount(accountId, customer, req.initialBalance(), overdraft);
        } else {
            throw new IllegalArgumentException("Invalid account type: " + req.accountType() + ". Supported types are SAVINGS or CURRENT.");
        }
        BankAccount savedAccount = accountRepository.save(account);
     // Record Account Creation Ledger Entry
        LedgerEntry entry = new LedgerEntry(
                savedAccount.getAccountId(),
                TransactionType.ACCOUNT_CREATION,
                savedAccount.getBalance(),
                BigDecimal.ZERO,
                savedAccount.getBalance(),
                "Initial deposit on account creation"
        );
        ledgerRepository.save(entry);

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BankAccount getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
    }

    // Direct operation accepting DTO
    public BankAccount credit(String accountId, AccountOperationRequest request) {
        return credit(accountId, request.amount());
    }

    // Helper method for raw BigDecimal operations
    public BankAccount credit(String accountId, BigDecimal amount) {
        BankAccount account = getAccount(accountId);
        BigDecimal balanceBefore = account.getBalance();
        account.credit(amount);
        BankAccount updatedAccount = accountRepository.save(account);

        // Record Credit Ledger Entry
        LedgerEntry entry = new LedgerEntry(
                accountId,
                TransactionType.CREDIT,
                amount,
                balanceBefore,
                updatedAccount.getBalance(),
                "Account credited with Rs " + amount
        );
        ledgerRepository.save(entry);
        return accountRepository.save(account);
    }

    // Direct operation accepting DTO
    public BankAccount debit(String accountId, AccountOperationRequest request) {
        return debit(accountId, request.amount());
    }

    // Helper method for raw BigDecimal operations
    public BankAccount debit(String accountId, BigDecimal amount) {
        BankAccount account = getAccount(accountId);
        BigDecimal balanceBefore = account.getBalance();
        account.debit(amount);
        BankAccount updatedAccount = accountRepository.save(account);
     // Record Debit Ledger Entry
        LedgerEntry entry = new LedgerEntry(
                accountId,
                TransactionType.DEBIT,
                amount,
                balanceBefore,
                updatedAccount.getBalance(),
                "Account debited with Rs " + amount
        );
        ledgerRepository.save(entry);
        return accountRepository.save(account);
    }

    public BigDecimal closeAccount(String accountId) {
        BankAccount account = getAccount(accountId);
        BigDecimal finalBalance = account.getBalance();

        // Record Account Closure Entry BEFORE deletion
        LedgerEntry entry = new LedgerEntry(
                accountId,
                TransactionType.ACCOUNT_CLOSURE,
                finalBalance,
                finalBalance,
                BigDecimal.ZERO,
                "Account closed and final balance disbursed"
        );
        ledgerRepository.save(entry);
        accountRepository.delete(account);
        return finalBalance;
    }

    public FixedDeposit openFixedDeposit(String accountId, FixedDepositRequest req) {
        BankAccount account = getAccount(accountId);
        
        // Debit principal amount from bank account balance using overdraft/savings validation
        account.debit(req.principalAmount());
        accountRepository.save(account);

        String depositId = "FD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        FixedDeposit fixedDeposit = new FixedDeposit(
                depositId,
                account,
                req.principalAmount(),
                req.interestRate(),
                req.durationYears()
        );

        return fdRepository.save(fixedDeposit);
    }
    @Transactional(readOnly = true)
    public Page<LedgerEntry> getAccountStatement(String accountId, int page, int size) {
        getAccount(accountId); // Validate existence
        return ledgerRepository.findByAccountIdOrderByTimestampDesc(accountId, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<FixedDeposit> getFixedDepositsForAccount(String accountId) {
        getAccount(accountId); // Ensures account exists before querying FD list
        return fdRepository.findByAccountAccountId(accountId);
    }
}