package com.lmello.dasto.expenses.dto.output;

import com.lmello.dasto.categories.Category;
import com.lmello.dasto.expenses.Expense;
import com.lmello.dasto.expenses.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        LocalDate date,
        String title,
        BigDecimal amount,
        ExpenseType type,
        String description,
        int installmentQuantity,
        LocalDate finalPayment,
        CategorySummary category,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {
    public ExpenseResponse(Expense expense) {
        this(
                expense.getId(),
                expense.getDate(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getType(),
                expense.getDescription(),
                expense.getInstallmentQuantity(),
                expense.getFinalPayment(),
                new CategorySummary(expense.getCategory()),
                expense.getCreatedAt(),
                expense.getCreatedBy(),
                expense.getUpdatedAt(),
                expense.getUpdatedBy()
        );
    }

    public record CategorySummary(Long id, String name) {
        public CategorySummary(Category category) {
            this(category.getId(), category.getName());
        }
    }
}
