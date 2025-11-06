package com.lmello.dasto.expenses;

import com.lmello.dasto.categories.Category;
import com.lmello.dasto.shared.entities.Auditable;
import com.lmello.dasto.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "expenses")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Expense extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 4000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime expenseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense expense)) return false;

        return id != null && id.equals(expense.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
