package com.lmello.dasto.budget.dto.output;

import com.lmello.dasto.budget.Budget;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetResponse(
        Long id,
        BigDecimal totalAmount,
        int investmentPercentage,
        BigDecimal investmentAmount,
        BigDecimal fixedExpenses,
        BigDecimal availableAmount,
        LocalDateTime effectiveDate,
        LocalDateTime terminationDate,
        boolean isActive,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy,
        LocalDateTime deletedAt,
        String deletedBy
) {
    public BudgetResponse(Budget budget) {
        this(
                budget.getId(),
                budget.getTotalAmount(),
                budget.getInvestmentPercentage(),
                budget.getInvestmentAmount(),
                budget.getFixedExpenses(),
                budget.getAvailableAmount(),
                budget.getEffectiveDate(),
                budget.getTerminationDate(),
                budget.isActive(),
                budget.getCreatedAt(),
                budget.getCreatedBy(),
                budget.getUpdatedAt(),
                budget.getUpdatedBy(),
                budget.getDeletedAt(),
                budget.getDeletedBy()
        );
    }
}
