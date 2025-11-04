package com.lmello.dasto.user.entities;

import com.lmello.dasto.entities.Auditable;
import com.lmello.dasto.categories.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SQLRestriction("deleted_at IS NULL OR deleted_at > current_timestamp")
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
    private List<Category> categories = new ArrayList<>();
    public void addCategory(Category category) {
        categories.add(category);
        category.setUser(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.setUser(null);
    }

    @PrePersist
    private void setPublicId() {
        if (publicId == null)
            publicId = UUID.randomUUID();
    }
}
