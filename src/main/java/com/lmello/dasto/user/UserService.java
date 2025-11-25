package com.lmello.dasto.user;

import com.lmello.dasto.user.dto.input.CreateUserDTO;
import com.lmello.dasto.user.dto.input.PatchUserDTO;
import com.lmello.dasto.user.exceptions.EmailInUseException;
import com.lmello.dasto.user.exceptions.UserAlreadyExistsException;
import com.lmello.dasto.user.exceptions.UserNotExistsException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById(UUID publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(UserNotExistsException::new);
    }

    @Transactional
    public User create(CreateUserDTO data) {
        final String email = data.email();
        final String firstName = data.firstName();
        final String lastName = data.lastName();
        final String passwordHash = passwordEncoder.encode(data.password());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        User u = new User();

        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setPasswordHash(passwordHash);

        return userRepository.save(u);
    }

    @Transactional
    public User patch(UUID publicId, PatchUserDTO data) {
        User existing = userRepository.findByPublicId(publicId)
                .orElseThrow(UserNotExistsException::new);

        if (data.firstName() != null && !data.firstName().equals(existing.getFirstName())) {
            existing.setFirstName(data.firstName());
        }

        if (!Objects.equals(data.lastName(), existing.getLastName())) {
            existing.setLastName(data.lastName());
        }

        if (data.email() != null && !data.email().equals(existing.getEmail())) {
            if (userRepository.existsByEmail(data.email())) {
                throw new EmailInUseException(data.email());
            }

            existing.setEmail(data.email());
        }

        if (data.password() != null) {
            String encodedPassword = passwordEncoder.encode(data.password());

            if (!passwordEncoder.matches(data.password(), existing.getPasswordHash())) {
                existing.setPasswordHash(encodedPassword);
            }
        }

        return userRepository.save(existing);
    }

    @Transactional
    public void delete(UUID publicId) {
        User u = userRepository.findByPublicId(publicId)
                .orElseThrow(UserNotExistsException::new);

        userRepository.delete(u);
    }
}
