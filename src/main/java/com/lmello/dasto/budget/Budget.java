package com.lmello.dasto.budget;

import com.lmello.dasto.shared.entities.Auditable;
import com.lmello.dasto.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "budgets")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal fixedExpenses;

    @Column(nullable = false)
    private int investmentPercentage;

    @Column(precision = 10, scale = 2)
    private BigDecimal investmentAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal availableAmount;

    @Column(nullable = false)
    private LocalDateTime effectiveDate = LocalDateTime.now();

    private LocalDateTime terminationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public boolean isActive() {
        LocalDateTime today = LocalDateTime.now();
        return (effectiveDate.isBefore(today) || effectiveDate.isEqual(today))
                && (terminationDate == null || terminationDate.isAfter(today));
    }

    public void terminate(LocalDateTime terminationDate) {
        this.terminationDate = terminationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Budget budget)) return false;

        return id != null && id.equals(budget.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
