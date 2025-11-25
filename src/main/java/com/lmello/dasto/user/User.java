package com.lmello.dasto.user;

import com.lmello.dasto.budget.Budget;
import com.lmello.dasto.categories.Category;
import com.lmello.dasto.shared.entities.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private UUID publicId;

    @Column(nullable = false, length = 128)
    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false, length = 128)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = addInitialCategories();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Budget> budgets = new ArrayList<>();

    public void addCategory(Category category) {
        categories.add(category);
        category.setUser(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.setUser(null);
    }

    public void addBudget(Budget budget) {
        budgets.add(budget);
        budget.setUser(this);
    }

    private List<Category> addInitialCategories() {
        return Stream.of(
                        "Food",
                        "Transportation",
                        "Housing",
                        "Utilities",
                        "Healthcare",
                        "Entertainment",
                        "Shopping",
                        "Education",
                        "Personal Care",
                        "Insurance",
                        "Savings & Investments",
                        "Debt Payments",
                        "Gifts & Donations",
                        "Travel",
                        "Subscriptions",
                        "Other"
                )
                .map(name -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setUser(this);

                    return c;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @PrePersist
    private void setPublicId() {
        if (publicId == null)
            publicId = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        return (id != null && id.equals(user.getId()))
                || (publicId != null && publicId.equals(user.getPublicId()));
    }
}
