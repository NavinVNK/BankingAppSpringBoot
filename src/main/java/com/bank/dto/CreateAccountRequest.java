package com.bank.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
@Schema(description = "Payload required to open a new Bank Account")
public record CreateAccountRequest(
	@Schema(description = "Account type", example = "SAVINGS", allowableValues = {"SAVINGS", "CURRENT"})
    @NotBlank String accountType, // "SAVINGS" or "CURRENT"
    
    @Schema(description = "Full name of the account holder", example = "John Doe")
    @NotBlank String name,

    @Schema(description = "Door number or flat details", example = "12/A")
    @NotBlank String doorNumber,

    @Schema(description = "Street name", example = "MG Road")
    @NotBlank String streetName,

    @Schema(description = "City name", example = "Madurai")
    @NotBlank String city,

    @Schema(description = "Postal Pin Code", example = "625001")
    @NotBlank String pinCode,

    @Schema(description = "Initial deposit amount (Minimum Rs 500 for Savings)", example = "1000.00")
    @NotNull @Min(500) BigDecimal initialBalance,

    @Schema(description = "Overdraft limit (Required only for CURRENT accounts)", example = "5000.00")
    BigDecimal overdraftLimit // Required if accountType is CURRENT
) {}


