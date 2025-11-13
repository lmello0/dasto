package com.lmello.dasto.budget.exceptions;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class NoBudgetAtDateException extends RuntimeException {

    private final UUID userId;
    private final LocalDate date;

    public NoBudgetAtDateException(UUID userId, LocalDate date) {
        this.userId = userId;
        this.date = date;

        super("Not found budget for user " + userId + " at date " + date);
    }
}