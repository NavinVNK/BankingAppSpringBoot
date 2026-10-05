package com.bank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AccountOperationRequest(
    @NotNull @Positive BigDecimal amount
) {}
