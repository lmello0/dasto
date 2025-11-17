package com.lmello.dasto.budget.dto.output;

import com.lmello.dasto.budget.Budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BudgetResponse(
        Long id,
        BigDecimal totalAmount,
        LocalDate effectiveDate,
        LocalDate terminationDate,
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
