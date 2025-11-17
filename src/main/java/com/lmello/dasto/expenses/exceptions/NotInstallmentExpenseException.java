package com.lmello.dasto.expenses.exceptions;

public class NotInstallmentExpenseException extends RuntimeException {
    public NotInstallmentExpenseException() {
        super("Expense is not a installment expense");
    }
}
