package com.lmello.dasto.budget.dto.input;

import com.lmello.dasto.shared.validators.decorators.AtLeastOne;
import com.lmello.dasto.shared.validators.decorators.MutuallyExclusive;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@MutuallyExclusive(
        fields = {"investmentPercentage", "investmentAmount"},
        message = "Provide either investmentPercentage or investmentAmount, not both"
)
@AtLeastOne(
        fields = {"totalAmount", "fixedExpenses", "investmentPercentage", "investmentAmount"}
)
public record PatchBudgetDTO(
        @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
        BigDecimal totalAmount,

        @PositiveOrZero(message = "Fixed expenses must be positive or zero")
        BigDecimal fixedExpenses,

        @Min(value = 0, message = "Investment percentage must be at least 0")
        @Max(value = 100, message = "Investment percentage cannot exceed 100")
        Integer investmentPercentage,

        @Positive(message = "Investment amount must be positive")
        BigDecimal investmentAmount
) {
}
