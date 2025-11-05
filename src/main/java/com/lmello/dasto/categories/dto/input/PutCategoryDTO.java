package com.lmello.dasto.categories.dto.input;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PutCategoryDTO(
        @NotEmpty(message = "A category must have a name")
        @Size(min = 3, max = 32, message = "Name must be between 3 and 32 characters long")
        String name
) {
}
