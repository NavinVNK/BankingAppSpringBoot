package com.bank.controller;

import com.bank.dto.*;
import com.bank.entity.BankAccount;
import com.bank.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
public class BankAccountController {

    private final BankAccountService accountService;

    public BankAccountController(BankAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<BankAccount> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        BankAccount account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getAccount(@PathVariable String id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<BankAccount> credit(@PathVariable String id, @Valid @RequestBody AccountOperationRequest req) {
        return ResponseEntity.ok(accountService.credit(id, req));
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<BankAccount> debit(@PathVariable String id, @Valid @RequestBody AccountOperationRequest req) {
        return ResponseEntity.ok(accountService.debit(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> closeAccount(@PathVariable String id) {
        BigDecimal finalBalance = accountService.closeAccount(id);
        return ResponseEntity.ok(Map.of(
                "message", "Account successfully closed",
                "accountId", id,
                "finalBalance", finalBalance
        ));
    }

    @PostMapping("/{id}/fixed-deposits")
    public ResponseEntity<FixedDepositResponse> createFixedDeposit(
            @PathVariable String id, 
            @Valid @RequestBody FixedDepositRequest req) {
        var fd = accountService.openFixedDeposit(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(FixedDepositResponse.fromEntity(fd));
    }

    @GetMapping("/{id}/fixed-deposits")
    public ResponseEntity<List<FixedDepositResponse>> getFixedDeposits(@PathVariable String id) {
        List<FixedDepositResponse> responses = accountService.getFixedDepositsForAccount(id)
                .stream()
                .map(FixedDepositResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }
}