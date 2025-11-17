package com.lmello.dasto.budget.exceptions;

import lombok.Getter;

@Getter
public class BudgetNotFoundException extends RuntimeException {

    private final Long budgetId;

    public BudgetNotFoundException(Long budgetId) {
        this.budgetId = budgetId;

        super("Budget " + budgetId + " not found for user");
    }
}