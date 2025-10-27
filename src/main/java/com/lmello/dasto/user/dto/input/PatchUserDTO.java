package com.lmello.dasto.user.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record PatchUserDTO(
        String firstName,
        String lastName,

        @Email(message = "'email' is not a valid email")
        String email,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&()\\[\\]{}#])[A-Za-z\\d@$!%*?&()\\[\\]{}#]{8,}$",
                message = "Password must have at least 1 lower, 1 upper, 1 number, 1 special and at least 8 chars"
        )
        String password
) {
}
