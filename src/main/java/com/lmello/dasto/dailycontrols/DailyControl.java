package com.lmello.dasto.dailycontrols;

import com.lmello.dasto.budget.Budget;
import com.lmello.dasto.expenses.Expense;
import com.lmello.dasto.shared.entities.Auditable;
import com.lmello.dasto.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "daily_controls")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyControl extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyLimit;

    @Column(nullable = false, scale = 10, precision = 2)
    private BigDecimal carriedOver = BigDecimal.ZERO;

    @Column(nullable = false, scale = 10, precision = 2)
    private BigDecimal adjustedLimit;

    @Column(nullable = false)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal remaining;

    @Column(nullable = false)
    private BigDecimal carryoverNext;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @OneToMany(mappedBy = "dailyControl", cascade = CascadeType.ALL)
    private List<Expense> expenses = new ArrayList<>();

    public void recalculate() {
        // calculate everything non deleted
        this.totalSpent = expenses.stream()
                .filter(e -> e.getDeletedAt() == null)
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // daily limit + carry over from previous
        this.adjustedLimit = dailyLimit.add(carriedOver);

        // what's left
        this.remaining = adjustedLimit.subtract(totalSpent);

        // carryover to next = remaining
        this.carryoverNext = remaining;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setDailyControl(this);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setDailyControl(null);
    }

    public boolean isOverBudget() {
        return remaining.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getPercentageUsed() {
        if (adjustedLimit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalSpent.divide(adjustedLimit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
