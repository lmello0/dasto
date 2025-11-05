package com.lmello.dasto.budget.exceptions;

import lombok.Getter;

@Getter
public class MultipleInvestmentValueException extends RuntimeException {
    public MultipleInvestmentValueException() {
        super("Either investmentAmount or investmentPercentage must be provided");
    }
}
