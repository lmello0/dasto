package com.lmello.dasto.categories.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryNotExistsException extends RuntimeException {
    private final UUID userId;
    private final Long categoryId;

    public CategoryNotExistsException(UUID userId, Long categoryId) {
        this.userId = userId;
        this.categoryId = categoryId;

        super("User " + userId + " does not have the category '" + categoryId + "' registered");
    }
}
