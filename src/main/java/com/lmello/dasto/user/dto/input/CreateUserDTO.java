package com.lmello.dasto.user.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserDTO(
        @NotBlank(message = "'firstName' cannot be blank")
        String firstName,

        String lastName,

        @NotBlank(message = "'email' cannot be blank")
        @Email(message = "'email' is not a valid email")
        String email,

        @NotBlank(message = "'password' cannot be blank")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&()\\[\\]{}#])[A-Za-z\\d@$!%*?&()\\[\\]{}#]{8,}$",
                message = "Password must have at least 1 lower, 1 upper, 1 number, 1 special and at least 8 chars"
        )
        String password
) {
}
