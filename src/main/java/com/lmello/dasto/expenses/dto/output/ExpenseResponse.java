package com.lmello.dasto.expenses.dto.output;

import com.lmello.dasto.categories.Category;
import com.lmello.dasto.expenses.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        String title,
        BigDecimal amount,
        String description,
        LocalDate expenseDate,
        CategorySummary category,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {
    public ExpenseResponse(Expense expense) {
        this(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getExpenseDate(),
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
