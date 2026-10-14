package com.bank.controller;

import com.bank.dto.*;
import com.bank.entity.BankAccount;
import com.bank.entity.FixedDeposit;
import com.bank.entity.LedgerEntry;
import com.bank.service.BankAccountService;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Bank Accounts", description = "Endpoints for account management, transactions, and fixed deposits")
public class BankAccountController {

    private final BankAccountService accountService;

    public BankAccountController(BankAccountService accountService) {
        this.accountService = accountService;
    }
    @Operation(summary = "Create a new Bank Account", description = "Creates a Savings or Current account. Minimum Rs 500 initial deposit required.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Account created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BankAccount.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Validation failure or maximum limit of 100 accounts reached",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @PostMapping
    public ResponseEntity<BankAccount> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        BankAccount account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    // ----------------------------------------------------------------------------------
    // 2. GET ACCOUNT DETAILS
    // ----------------------------------------------------------------------------------
    @Operation(summary = "Fetch account by ID", description = "Retrieves profile and current balance of a bank account.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Account retrieved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BankAccount.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Account ID not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        )
    })

    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getAccount(@PathVariable String id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }
    // ----------------------------------------------------------------------------------
    // 3. CREDIT AMOUNT
    // ----------------------------------------------------------------------------------
    @Operation(summary = "Credit amount to account", description = "Credits balance into the account. Clears overdraft dues first if it is a Current account.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Account credited successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BankAccount.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid credit amount",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Account ID not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        )
    })

    @PostMapping("/{id}/credit")
    public ResponseEntity<BankAccount> credit(@PathVariable String id, @Valid @RequestBody AccountOperationRequest req) {
        return ResponseEntity.ok(accountService.credit(id, req));
    }
    // ----------------------------------------------------------------------------------
    // 4. DEBIT AMOUNT
    // ----------------------------------------------------------------------------------
    @Operation(summary = "Debit amount from account", description = "Debits amount subject to Rs 500 minimum balance for Savings or Overdraft limit for Current.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Account debited successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BankAccount.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Insufficient funds or minimum balance constraint violated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Account ID not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        )
    })

    @PostMapping("/{id}/debit")
    public ResponseEntity<BankAccount> debit(@PathVariable String id, @Valid @RequestBody AccountOperationRequest req) {
        return ResponseEntity.ok(accountService.debit(id, req));
    }
    // ----------------------------------------------------------------------------------
    // 5. ACCOUNT CLOSURE
    // ----------------------------------------------------------------------------------
    @Operation(summary = "Close account", description = "Deletes account from system and returns final balance.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Account closed successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Account ID not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        )
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> closeAccount(@PathVariable String id) {
        BigDecimal finalBalance = accountService.closeAccount(id);
        return ResponseEntity.ok(Map.of(
                "message", "Account successfully closed",
                "accountId", id,
                "finalBalance", finalBalance
        ));
    }
 // ----------------------------------------------------------------------------------
    // 6. OPEN FIXED DEPOSIT
    // ----------------------------------------------------------------------------------
    @Operation(summary = "Open Fixed Deposit", description = "Debits principal amount from account and initializes a Fixed Deposit record.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Fixed Deposit created successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FixedDeposit.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Insufficient balance in account for FD principal",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Account ID not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        )
    })

    @PostMapping("/{id}/fixed-deposits")
    public ResponseEntity<FixedDepositResponse> createFixedDeposit(
            @PathVariable String id, 
            @Valid @RequestBody FixedDepositRequest req) {
        var fd = accountService.openFixedDeposit(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(FixedDepositResponse.fromEntity(fd));
    }
 // ----------------------------------------------------------------------------------
    // 7. GET FIXED DEPOSIT ACCOUNT DETAILS
    // ----------------------------------------------------------------------------------
    @Operation(summary = "Fetch Fixed deposit account by ID", description = "Retrieves profile and current balance of a bank account.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Fixed deposit account retrieved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FixedDeposit.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Fixed deposit account ID not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @GetMapping("/{id}/fixed-deposits")
    public ResponseEntity<List<FixedDepositResponse>> getFixedDeposits(@PathVariable String id) {
        List<FixedDepositResponse> responses = accountService.getFixedDepositsForAccount(id)
                .stream()
                .map(FixedDepositResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/{id}/statement")
    public ResponseEntity<Page<LedgerEntry>> getAccountStatement(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LedgerEntry> statement = accountService.getAccountStatement(id, page, size);
        return ResponseEntity.ok(statement);
    }
}