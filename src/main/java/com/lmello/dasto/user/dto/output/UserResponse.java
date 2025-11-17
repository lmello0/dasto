package com.lmello.dasto.user.dto.output;

import com.lmello.dasto.user.User;

import java.time.LocalDateTime;

public record UserResponse(
        String publicId,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy,
        LocalDateTime deletedAt,
        String deletedBy
) {
    public UserResponse(User user) {
        this(
                user.getPublicId().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getCreatedBy(),
                user.getUpdatedAt(),
                user.getUpdatedBy(),
                user.getDeletedAt(),
                user.getDeletedBy()
        );
    }
}
