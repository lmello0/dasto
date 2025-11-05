package com.lmello.dasto.categories.dto.output;

import com.lmello.dasto.categories.Category;
import com.lmello.dasto.expenses.Expense;

import java.util.List;

public record CategoryDetailResponse(
        Long id,
        String name,
        // TODO: change to ExpenseDetail
        List<Expense> expenses
) {
    public CategoryDetailResponse(Category c) {
        this(
                c.getId(),
                c.getName(),
                c.getExpenses()
        );
    }
}
