package com.lmello.dasto.expenses;

import com.lmello.dasto.categories.Category;
import com.lmello.dasto.categories.CategoryService;
import com.lmello.dasto.dailycontrols.DailyControl;
import com.lmello.dasto.dailycontrols.DailyControlService;
import com.lmello.dasto.expenses.dto.input.CreateExpenseDTO;
import com.lmello.dasto.expenses.dto.input.UpdateExpenseDTO;
import com.lmello.dasto.expenses.exceptions.ExpenseNotFoundException;
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

    private final DailyControlService dailyControlService;
    private final CategoryService categoryService;
    private final UserService userService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            DailyControlService dailyControlService,
            CategoryService categoryService,
            UserService userService
    ) {
        this.expenseRepository = expenseRepository;

        this.dailyControlService = dailyControlService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    public Page<Expense> getUserExpenses(UUID userId, Pageable pageable) {
        User user = userService.getUserById(userId);
        return expenseRepository.findByUserOrderByExpenseDateDesc(user, pageable);
    }

    public List<Expense> getExpensesByDate(UUID userId, LocalDate date) {
        User user = userService.getUserById(userId);
        return expenseRepository.findByUserAndExpenseDate(user, date);
    }

    public List<Expense> getExpensesByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        User user = userService.getUserById(userId);
        return expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate);
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
        User user = userService.getUserById(userId);
        Category category = categoryService.getUserCategory(user, data.categoryId());

        DailyControl dailyControl = dailyControlService
                .getOrCreateDailyControl(userId, data.expenseDate());

        Expense expense = new Expense();
        expense.setTitle(data.title());
        expense.setAmount(data.amount());
        expense.setDescription(data.description());
        expense.setExpenseDate(data.expenseDate());
        expense.setCategory(category);
        expense.setUser(user);

        dailyControl.addExpense(expense);

        Expense saved = expenseRepository.save(expense);

        dailyControlService.recalculateFromDate(user, data.expenseDate());

        return saved;
    }

    @Transactional
    public Expense updateExpense(UUID userId, Long expenseId, UpdateExpenseDTO data) {
        User user = userService.getUserById(userId);

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(userId, expenseId));

        LocalDate oldDate = expense.getExpenseDate();
        LocalDate newDate = data.expenseDate();
        boolean dateChanged = !oldDate.equals(newDate);

        if (dateChanged) {
            DailyControl oldControl = expense.getDailyControl();
            oldControl.removeExpense(expense);

            DailyControl newControl = dailyControlService
                    .getOrCreateDailyControl(user, newDate);
            newControl.addExpense(expense);
        }

        expense.setTitle(data.title());
        expense.setAmount(data.amount());
        expense.setDescription(data.description());
        expense.setExpenseDate(newDate);

        if (data.categoryId() != null) {
            Category category = categoryService.getUserCategory(user, data.categoryId());
            expense.setCategory(category);
        }

        Expense saved = expenseRepository.save(expense);

        if (dateChanged) {
            LocalDate earliestDate = oldDate.isBefore(newDate) ? oldDate : newDate;
            dailyControlService.recalculateFromDate(user, earliestDate);
        } else {
            dailyControlService.recalculateFromDate(user, newDate);
        }

        return saved;
    }

    @Transactional
    public void deleteExpense(UUID userId, Long expenseId) {
        User user = userService.getUserById(userId);

        Expense expense = expenseRepository.findByUserAndId(user, expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(userId, expenseId));

        LocalDate expenseDate = expense.getExpenseDate();

        expenseRepository.delete(expense);

        dailyControlService.recalculateFromDate(user, expenseDate);
    }

    public BigDecimal getMonthlyTotal(UUID userId, YearMonth yearMonth) {
        User user = userService.getUserById(userId);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return expenseRepository.sumByUserAndDateRange(user, startDate, endDate)
                .orElse(BigDecimal.ZERO);
    }
}
