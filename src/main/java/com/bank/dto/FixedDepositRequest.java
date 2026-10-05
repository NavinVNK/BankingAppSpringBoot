package com.bank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FixedDepositRequest(
	    @NotNull @Positive BigDecimal principalAmount,
	    @NotNull @Positive BigDecimal interestRate,
	    @NotNull @Min(1) Integer durationYears
	) {}

