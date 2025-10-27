package com.lmello.dasto.user.entities;

import com.lmello.dasto.entities.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

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

    @PrePersist
    private void setPublicId() {
        if (publicId == null)
            publicId = UUID.randomUUID();
    }
}
