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
import java.time.LocalDate;

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

    @Column(name = "expense_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 32)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Embedded
    @Column(nullable = false)
    private ExpenseType type;

    @Column(length = 4000)
    private String description;

    private Integer installmentQuantity;

    private LocalDate finalPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
