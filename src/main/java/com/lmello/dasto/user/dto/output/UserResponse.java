package com.lmello.dasto.user.dto.output;

import com.lmello.dasto.user.User;

public record UserResponse(
        String publicId,
        String firstName,
        String lastName,
        String email
) {
    public UserResponse(User saved) {
        this(
                saved.getPublicId().toString(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getEmail()
        );
    }
}
