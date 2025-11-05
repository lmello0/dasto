package com.lmello.dasto.budget;

import com.lmello.dasto.budget.dto.input.CreateBudgetDTO;
import com.lmello.dasto.budget.exceptions.ActiveBudgetNotFoundException;
import com.lmello.dasto.budget.exceptions.MultipleInvestmentValueException;
import com.lmello.dasto.user.User;
import com.lmello.dasto.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final UserService userService;

    public BudgetService(BudgetRepository budgetRepository, UserService userService) {
        this.budgetRepository = budgetRepository;
        this.userService = userService;
    }

    public Budget getActiveBudget(UUID userId) {
        User user = userService.getUserById(userId);
        return budgetRepository.findActiveByUser(user)
                .orElseThrow(() -> new ActiveBudgetNotFoundException(userId));
    }

    public Page<Budget> getBudgetHistory(UUID userId, Pageable pageable) {
        User user = userService.getUserById(userId);
        return budgetRepository.findByUserOrderByEffectiveDateDesc(user, pageable);
    }

    @Transactional
    public Budget createBudget(UUID userId, CreateBudgetDTO data) {
        User user = userService.getUserById(userId);

        LocalDateTime effectiveDate = data.effectiveDate() != null
                ? data.effectiveDate().atStartOfDay()
                : LocalDateTime.now();

        budgetRepository.findActiveByUser(user).ifPresent(existingBudget -> {
            existingBudget.terminate(effectiveDate.minusSeconds(1));
            existingBudget.setDeletedAt(OffsetDateTime.now());
            existingBudget.setDeletedBy("API");
            budgetRepository.save(existingBudget);
        });

        Budget budget = buildBudgetFromData(user, data, effectiveDate);

        return budgetRepository.save(budget);
    }

    private Budget buildBudgetFromData(User user, CreateBudgetDTO data, LocalDateTime effectiveDate) {
        BigDecimal total = data.totalAmount();

        BigDecimal fixedExpenses = data.fixedExpenses() != null ? data.fixedExpenses() : BigDecimal.ZERO;
        BigDecimal investmentAmount = calculateInvestmentAmount(data, fixedExpenses, total);
        int investmentPercentage = calculateInvestmentPercentage(data, investmentAmount, total);

        BigDecimal availableAmount = total
                .subtract(investmentAmount)
                .subtract(fixedExpenses)
                .setScale(2, RoundingMode.HALF_UP);

        Budget budget = new Budget();
        budget.setTotalAmount(total);
        budget.setFixedExpenses(fixedExpenses);
        budget.setInvestmentAmount(investmentAmount.setScale(2, RoundingMode.HALF_UP));
        budget.setInvestmentPercentage(investmentPercentage);
        budget.setAvailableAmount(availableAmount);
        budget.setEffectiveDate(effectiveDate);
        budget.setTerminationDate(null);

        user.addBudget(budget);

        return budget;
    }

    private BigDecimal calculateInvestmentAmount(CreateBudgetDTO data, BigDecimal fixedExpenses, BigDecimal total) {
        if (data.investmentPercentage() != null) {
            return total
                    .subtract(fixedExpenses)
                    .multiply(BigDecimal.valueOf(data.investmentPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        if (data.investmentAmount() == null) {
            throw new MultipleInvestmentValueException();
        }

        return data.investmentAmount();
    }

    private int calculateInvestmentPercentage(CreateBudgetDTO data,
                                              BigDecimal investmentAmount,
                                              BigDecimal total) {
        if (data.investmentPercentage() != null) {
            return data.investmentPercentage();
        }

        BigDecimal percentage = investmentAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(total, 0, RoundingMode.HALF_UP);

        return percentage.intValueExact();
    }
}
