package com.bank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateAccountRequest(
    @NotBlank String accountType, // "SAVINGS" or "CURRENT"
    @NotBlank String name,
    @NotBlank String doorNumber,
    @NotBlank String streetName,
    @NotBlank String city,
    @NotBlank String pinCode,
    @NotNull @Min(500) BigDecimal initialBalance,
    BigDecimal overdraftLimit // Required if accountType is CURRENT
) {}


