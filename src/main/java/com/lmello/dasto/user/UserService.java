package com.lmello.dasto.user;

import com.lmello.dasto.user.dto.input.CreateUserDTO;
import com.lmello.dasto.user.dto.input.PatchUserDTO;
import com.lmello.dasto.user.dto.output.UserResponse;
import com.lmello.dasto.user.exceptions.EmailInUseException;
import com.lmello.dasto.user.exceptions.InvalidPublicIdException;
import com.lmello.dasto.user.exceptions.UserAlreadyExistsException;
import com.lmello.dasto.user.exceptions.UserNotExistsException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(UserResponse::new);
    }

    public UserResponse getUserById(String publicId) {
        UUID uuid = toUUID(publicId);

        User u = userRepository.findByPublicId(uuid)
                .orElseThrow(UserNotExistsException::new);

        return new UserResponse(u);
    }

    @Transactional
    public UserResponse create(CreateUserDTO data) {
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

        User saved = userRepository.save(u);

        return new UserResponse(saved);
    }

    @Transactional
    public UserResponse patch(String publicId, PatchUserDTO data) {
        UUID uuid = toUUID(publicId);

        User existing = userRepository.findByPublicId(uuid)
                .orElseThrow(UserNotExistsException::new);

        if (!data.firstName().equals(existing.getFirstName())) {
            existing.setFirstName(data.firstName());
        }

        if (
                data.lastName() == null || existing.getLastName() != null && !data.lastName().equals(existing.getLastName())
        ) {
            existing.setLastName(data.lastName());
        }

        if (!data.email().equals(existing.getEmail())) {
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

        User saved = userRepository.save(existing);

        return new UserResponse(saved);
    }

    @Transactional
    public void delete(String publicId) {
        UUID uuid = toUUID(publicId);

        User u = userRepository.findByPublicId(uuid)
                .orElseThrow(UserNotExistsException::new);

        u.setDeletedAt(OffsetDateTime.now());
        userRepository.save(u);
    }

    private UUID toUUID(String stringUuid) {
        UUID uuid;
        try {
            uuid = UUID.fromString(stringUuid);
        } catch (IllegalArgumentException e) {
            throw new InvalidPublicIdException(stringUuid);
        }

        return uuid;
    }
}
