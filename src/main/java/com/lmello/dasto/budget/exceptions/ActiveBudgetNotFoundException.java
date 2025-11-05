package com.lmello.dasto.budget.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ActiveBudgetNotFoundException extends RuntimeException {

    private final UUID userId;

    public ActiveBudgetNotFoundException(UUID userId) {
        this.userId = userId;

        super("Active budget not found for user " + userId);
    }
}