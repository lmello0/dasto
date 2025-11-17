package com.lmello.dasto.budget;

import com.lmello.dasto.budget.dto.input.CreateBudgetDTO;
import com.lmello.dasto.budget.dto.input.PatchBudgetDTO;
import com.lmello.dasto.budget.exceptions.ActiveBudgetNotFoundException;
import com.lmello.dasto.budget.exceptions.BudgetNotFoundException;
import com.lmello.dasto.budget.exceptions.NoBudgetAtDateException;
import com.lmello.dasto.user.User;
import com.lmello.dasto.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        budgetRepository.findActiveByUser(user).ifPresent(existingBudget -> {
            existingBudget.terminate(data.effectiveDate());
            budgetRepository.save(existingBudget);
        });

        Budget budget = new Budget(data);

        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget putBudget(UUID userId, Long budgetId, PatchBudgetDTO data) {
        User user = userService.getUserById(userId);

        Budget budget = budgetRepository.findByUserAndId(user, budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));

        budget.setTotalAmount(data.totalAmount());
        return budgetRepository.save(budget);
    }

    public Budget getActiveBudgetAtDate(User user, LocalDate date) {
        return budgetRepository.findByUserActiveAtDate(user, date)
                .orElseThrow(() -> new NoBudgetAtDateException(user.getPublicId(), date));
    }
}
