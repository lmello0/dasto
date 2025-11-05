package com.lmello.dasto.budget;

import com.lmello.dasto.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    @Query("""
            SELECT b FROM Budget b
            WHERE b.user = :user
            AND b.effectiveDate <= CURRENT_TIMESTAMP
            AND (b.terminationDate IS NULL OR b.terminationDate > CURRENT_TIMESTAMP)
            ORDER BY b.effectiveDate DESC
            """)
    Optional<Budget> findActiveByUser(@Param("user") User user);

    Page<Budget> findByUserOrderByEffectiveDateDesc(User user, Pageable pageable);
}
