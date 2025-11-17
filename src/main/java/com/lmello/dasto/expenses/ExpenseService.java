package com.lmello.dasto.expenses;

import com.lmello.dasto.categories.Category;
import com.lmello.dasto.categories.CategoryService;
import com.lmello.dasto.expenses.dto.input.CreateExpenseDTO;
import com.lmello.dasto.expenses.dto.input.PatchExpenseDTO;
import com.lmello.dasto.expenses.exceptions.ExpenseNotFoundException;
import com.lmello.dasto.expenses.exceptions.NotInstallmentExpenseException;
import com.lmello.dasto.expenses.exceptions.RequiredInstallmentQuantityException;
import com.lmello.dasto.user.User;
import com.lmello.dasto.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final CategoryService categoryService;
    private final UserService userService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            CategoryService categoryService,
            UserService userService
    ) {
        this.expenseRepository = expenseRepository;

        this.categoryService = categoryService;
        this.userService = userService;
    }

    public Page<Expense> getUserExpenses(UUID userId, Pageable pageable) {
        User user = userService.getUserById(userId);
        return expenseRepository.findByUserOrderByDateDesc(user, pageable);
    }

    public List<Expense> getExpensesByDate(UUID userId, LocalDate date) {
        User user = userService.getUserById(userId);
        return expenseRepository.findByUserAndDate(user, date);
    }

    public List<Expense> getExpensesByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserById(userId);
        return expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
    }

    public Page<Expense> getExpensesByCategory(UUID userId, Long categoryId, Pageable pageable) {
        User user = userService.getUserById(userId);
        Category category = categoryService.getUserCategory(user, categoryId);

        return expenseRepository.findByUserAndCategory(user, category, pageable);
    }

    public Expense getExpenseById(UUID userId, Long expenseId) {
        User user = userService.getUserById(userId);

        return expenseRepository.findByUserAndId(user, expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(userId, expenseId));
    }

    @Transactional
    public Expense createExpense(UUID userId, CreateExpenseDTO data) {
        if (data.type() == ExpenseType.INSTALLMENT && data.installmentQuantity() == null) {
            throw new RequiredInstallmentQuantityException();
        }

        if (data.type() != ExpenseType.INSTALLMENT && data.installmentQuantity() != null) {
            throw new NotInstallmentExpenseException();
        }

        User user = userService.getUserById(userId);
        Category category = categoryService.getUserCategory(user, data.categoryId());

        Expense expense = new Expense();

        expense.setDate(data.date());
        expense.setTitle(data.title());
        expense.setAmount(data.amount());
        expense.setType(data.type());
        expense.setDescription(data.description());
        expense.setInstallmentQuantity(data.installmentQuantity());

        if (data.type() == ExpenseType.INSTALLMENT) {
            expense.setFinalPayment(data.date().plusMonths(data.installmentQuantity()));
        }

        expense.setCategory(category);
        expense.setUser(user);

        return expenseRepository.save(expense);
    }

    @Transactional
    public Expense patchExpense(UUID userId, Long expenseId, PatchExpenseDTO data) {
        User user = userService.getUserById(userId);
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(userId, expenseId));

        ExpenseType finalType = data.type() != null ? data.type() : expense.getType();

        if (finalType == ExpenseType.INSTALLMENT) {
            Integer finalInstallmentQuantity = data.installmentQuantity() != null
                    ? data.installmentQuantity()
                    : expense.getInstallmentQuantity();

            if (finalInstallmentQuantity == null || finalInstallmentQuantity <= 0) {
                throw new RequiredInstallmentQuantityException();
            }
        }

        if (finalType != ExpenseType.INSTALLMENT && data.installmentQuantity() != null) {
            throw new NotInstallmentExpenseException();
        }

        if (data.date() != null) {
            expense.setDate(data.date());
        }

        if (data.title() != null) {
            expense.setTitle(data.title());
        }

        if (data.amount() != null) {
            expense.setAmount(data.amount());
        }

        if (data.type() != null) {
            expense.setType(data.type());
        }

        if (data.description() != null) {
            expense.setDescription(data.description());
        }

        if (data.installmentQuantity() != null) {
            expense.setInstallmentQuantity(data.installmentQuantity());
        } else if (finalType != ExpenseType.INSTALLMENT) {
            expense.setInstallmentQuantity(null);
        }

        if (data.categoryId() != null) {
            Category category = categoryService.getUserCategory(user, data.categoryId());
            expense.setCategory(category);
        }

        if (expense.getType() == ExpenseType.INSTALLMENT) {
            LocalDate dateToUse = data.date() != null ? data.date() : expense.getDate();
            int installments = data.installmentQuantity() != null
                    ? data.installmentQuantity()
                    : expense.getInstallmentQuantity();

            expense.setFinalPayment(dateToUse.plusMonths(installments));
        } else {
            expense.setFinalPayment(null);
        }

        return expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(UUID userId, Long expenseId) {
        User user = userService.getUserById(userId);

        Expense expense = expenseRepository.findByUserAndId(user, expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(userId, expenseId));

        expenseRepository.delete(expense);
    }

    public BigDecimal getMonthlyTotal(UUID userId, YearMonth yearMonth) {
        User user = userService.getUserById(userId);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return expenseRepository.sumByUserAndDateRange(user, startDate, endDate)
                .orElse(BigDecimal.ZERO);
    }
}
