package com.lmello.dasto.categories.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryAlreadyExistsException extends RuntimeException {
    private final String categoryName;
    private final UUID userId;

    public CategoryAlreadyExistsException(UUID userId, String categoryName) {
        this.userId = userId;
        this.categoryName = categoryName;

        super("User " + userId + " already have the category '" + categoryName + "' registered");
    }
}
