package com.lmello.dasto.budget.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lmello.dasto.shared.validators.decorators.AtLeastOne;
import com.lmello.dasto.shared.validators.decorators.MutuallyExclusive;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@MutuallyExclusive(
        fields = {"investmentPercentage", "investmentAmount"},
        message = "Provide either investmentPercentage or investmentAmount, not both"
)
@AtLeastOne(
        fields = {"investmentPercentage", "investmentAmount"},
        message = "investmentPercentage or investmentAmount must be provided"
)
public record CreateBudgetDTO(
        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
        BigDecimal totalAmount,

        @Min(value = 0, message = "Investment percentage must be at least 0")
        @Max(value = 100, message = "Investment percentage cannot exceed 100")
        Integer investmentPercentage,

        @NotNull(message = "Effective date is required")
        @FutureOrPresent(message = "New budget must be at present or future")
        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate effectiveDate,

        @PositiveOrZero(message = "Fixed expenses must be positive or zero")
        BigDecimal fixedExpenses,

        @Positive(message = "Investment amount must be positive")
        BigDecimal investmentAmount
) {
}
