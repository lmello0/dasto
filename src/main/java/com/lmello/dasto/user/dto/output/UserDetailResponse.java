package com.lmello.dasto.user.dto.output;

import com.lmello.dasto.budget.Budget;
import com.lmello.dasto.categories.Category;
import com.lmello.dasto.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UserDetailResponse(
        String publicId,
        String firstName,
        String lastName,
        String email,
        BudgetResponse activeBudget,
        List<CategoryResponse> categories
) {
    public UserDetailResponse(User user) {
        this(
                user.getPublicId().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBudgets().stream()
                        .filter(Budget::isActive)
                        .findFirst()
                        .map(BudgetResponse::new)
                        .orElse(null),
                user.getCategories()
                        .stream()
                        .map(CategoryResponse::new)
                        .toList()
        );
    }

    private record BudgetResponse(
            BigDecimal totalAmount,
            int investmentPercentage,
            BigDecimal investmentAmount,
            BigDecimal fixedExpenses,
            BigDecimal availableAmount,
            LocalDateTime effectiveDate,
            LocalDateTime terminationDate,
            boolean isActive
    ) {
        public BudgetResponse(Budget b) {
            this(
                    b.getTotalAmount(),
                    b.getInvestmentPercentage(),
                    b.getInvestmentAmount(),
                    b.getFixedExpenses(),
                    b.getAvailableAmount(),
                    b.getEffectiveDate(),
                    b.getTerminationDate(),
                    b.isActive()
            );
        }
    }

    private record CategoryResponse(
            Long id,
            String name
    ) {
        public CategoryResponse(Category c) {
            this(c.getId(), c.getName());
        }
    }
}
