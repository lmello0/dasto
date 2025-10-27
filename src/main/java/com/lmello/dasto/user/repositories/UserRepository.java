package com.lmello.dasto.user.repositories;

import com.lmello.dasto.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByPublicId(UUID uuid);
}
