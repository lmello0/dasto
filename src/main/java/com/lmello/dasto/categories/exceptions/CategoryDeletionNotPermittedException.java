package com.lmello.dasto.categories.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryDeletionNotPermittedException extends RuntimeException {
    private final UUID userId;
    private final Long categoryId;

    public CategoryDeletionNotPermittedException(UUID userId, Long categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;

        super("User " + userId + " still have expenses in category '" + categoryId + "', deletion is not permitted.");
    }
}
