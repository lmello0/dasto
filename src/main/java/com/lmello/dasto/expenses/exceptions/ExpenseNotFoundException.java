package com.lmello.dasto.expenses.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ExpenseNotFoundException extends RuntimeException {

    private final UUID userId;
    private final Long expenseId;

    public ExpenseNotFoundException(UUID userId, Long expenseId) {
        this.userId = userId;
        this.expenseId = expenseId;

        super("Expense " + expenseId + " not found for user " + userId);
    }
}
