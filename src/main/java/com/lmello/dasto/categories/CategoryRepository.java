package com.lmello.dasto.categories;

import com.lmello.dasto.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAllByUser(User user, Pageable pageable);

    Optional<Category> findByUserAndId(User user, Long categoryId);

    boolean existsByUserAndName(User user, String name);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.category = :category")
    int countExpensesByCategory(Category category);
}
