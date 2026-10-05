package com.bank.dto;

import com.bank.entity.FixedDeposit;

import java.math.BigDecimal;
import java.time.Instant;

public record FixedDepositResponse(
        String depositId,
        String accountId,
        BigDecimal principalAmount,
        BigDecimal interestRate,
        Integer durationYears,
        BigDecimal computedInterest,
        BigDecimal maturityAmount,
        Instant createdAt
) {
    public static FixedDepositResponse fromEntity(FixedDeposit fd) {
        return new FixedDepositResponse(
                fd.getDepositId(),
                fd.getAccount().getAccountId(),
                fd.getPrincipalAmount(),
                fd.getInterestRate(),
                fd.getDurationYears(),
                fd.calculateInterest(),
                fd.getMaturityAmount(),
                fd.getCreatedAt()
        );
    }
}
