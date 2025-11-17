package com.lmello.dasto.expenses.exceptions;

public class RequiredInstallmentQuantityException extends RuntimeException {
    public RequiredInstallmentQuantityException() {
        super("Installment expenses must have a installment quantity");
    }
}
