package com.bank.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AccountOperationRequest(
	@Schema(description = "Amount to credit or debit", example = "250.00")		
    @NotNull @Positive BigDecimal amount
) {}
