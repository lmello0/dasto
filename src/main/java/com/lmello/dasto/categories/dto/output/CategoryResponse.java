package com.lmello.dasto.categories.dto.output;

import com.lmello.dasto.categories.Category;

public record CategoryResponse(
        Long id,
        String name
) {
    public CategoryResponse(Category c) {
        this(
                c.getId(),
                c.getName()
        );
    }
}
