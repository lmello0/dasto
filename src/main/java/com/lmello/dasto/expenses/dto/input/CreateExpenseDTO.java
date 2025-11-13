package com.lmello.dasto.expenses.dto.input;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseDTO(
        @NotBlank(message = "Title is required")
        @Size(max = 32, message = "Title must not exceed 32 characters")
        String title,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @Size(max = 4000, message = "Description must not exceed 4000 characters")
        String description,

        @NotNull(message = "Expense date is required")
        @PastOrPresent(message = "Expense date cannot be in the future")
        LocalDate expenseDate,

        @NotNull(message = "Category is required")
        Long categoryId
) {
}
