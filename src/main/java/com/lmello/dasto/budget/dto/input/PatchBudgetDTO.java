package com.lmello.dasto.budget.dto.input;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record PatchBudgetDTO(
        @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
        BigDecimal totalAmount
) {
}
