package com.lmello.dasto.budget.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBudgetDTO(
        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
        BigDecimal totalAmount,

        @NotNull(message = "Effective date is required")
        @FutureOrPresent(message = "Budget effective date must be at present or future")
        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate effectiveDate,

        @FutureOrPresent(message = "Budget termination date must be at present or future")
        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate terminationDate
) {
}
