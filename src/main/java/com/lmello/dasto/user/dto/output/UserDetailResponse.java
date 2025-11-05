package com.lmello.dasto.user.dto.output;

import com.lmello.dasto.categories.dto.output.CategoryResponse;
import com.lmello.dasto.user.User;

import java.util.List;

public record UserDetailResponse(
        String publicId,
        String firstName,
        String lastName,
        String email,
        List<CategoryResponse> categories
) {
    public UserDetailResponse(User user) {
        this(
                user.getPublicId().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCategories()
                        .stream()
                        .map(CategoryResponse::new)
                        .toList()
        );
    }
}
