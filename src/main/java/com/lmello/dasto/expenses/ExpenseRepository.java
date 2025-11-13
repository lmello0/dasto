package com.lmello.dasto.expenses;

import com.lmello.dasto.categories.Category;
import com.lmello.dasto.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByUserOrderByExpenseDateDesc(User user, Pageable pageable);

    List<Expense> findByUserAndExpenseDate(User user, LocalDate date);

    List<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate);

    Page<Expense> findByUserAndCategory(User user, Category category, Pageable pageable);

    Optional<Expense> findByUserAndId(User user, Long expenseId);

    @Query("""
            SELECT SUM(e.amount)
            FROM Expense e
            WHERE e.user = :user
            AND e.expenseDate BETWEEN :startDate AND :endDate
            """)
    Optional<BigDecimal> sumByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate);
}
