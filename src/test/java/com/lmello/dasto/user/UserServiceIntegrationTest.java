package com.lmello.dasto.user;

import com.lmello.dasto.user.dto.input.CreateUserDTO;
import com.lmello.dasto.user.dto.input.PatchUserDTO;
import com.lmello.dasto.user.exceptions.EmailInUseException;
import com.lmello.dasto.user.exceptions.UserAlreadyExistsException;
import com.lmello.dasto.user.exceptions.UserNotExistsException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CreateUserDTO defaultCreateDTO;

    @BeforeEach()
    void setUp() {
        defaultCreateDTO = new CreateUserDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                "(securePass123)"
        );
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create user successfully with valid data")
        void shouldCreateUserSuccessfully() {
            User createdUser = userService.create(defaultCreateDTO);

            assertThat(createdUser).isNotNull();
            assertThat(createdUser.getId()).isNotNull();
            assertThat(createdUser.getPublicId()).isNotNull();
            assertThat(createdUser.getEmail()).isEqualTo(defaultCreateDTO.email());
            assertThat(createdUser.getFirstName()).isEqualTo(defaultCreateDTO.firstName());

            // Password and password hash should differ
            assertThat(createdUser.getPasswordHash()).isNotEqualTo(defaultCreateDTO.password());
            assertThat(passwordEncoder.matches(defaultCreateDTO.password(), createdUser.getPasswordHash())).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowException_WhenEmailExists() {
            userService.create(defaultCreateDTO);

            assertThatThrownBy(() -> userService.create(defaultCreateDTO))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining(defaultCreateDTO.email());
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetTests {

        @Test
        @DisplayName("Should return user by Public ID")
        void shouldReturnUserByPublicId() {
            User savedUser = userService.create(defaultCreateDTO);

            User foundUser = userService.getUserById(savedUser.getPublicId());

            assertThat(foundUser).isEqualTo(savedUser);
        }

        @Test
        @DisplayName("Should throw exception when User ID does not exists")
        void shouldThrowException_WhenUserNotFound() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> userService.getUserById(randomId))
                    .isInstanceOf(UserNotExistsException.class);
        }

        @Test
        @DisplayName("Should return paged users")
        void shouldReturnPagedUsers() {
            userRepository.deleteAll();
            userService.create(new CreateUserDTO("u1@test.com", "A", "B", "(PasswordTest1)"));
            userService.create(new CreateUserDTO("u2@test.com", "C", "D", "(PasswordTest2)"));
            userService.create(new CreateUserDTO("u3@test.com", "E", "F", "(PasswordTest3)"));

            PageRequest pageable = PageRequest.of(0, 2);

            Page<User> result = userService.getAllUsers(pageable);

            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Patch User Tests")
    class PatchTests {

        @Test
        @DisplayName("Should update all fields when valid data is provided")
        void shouldUpdateAllFields() {
            User original = userService.create(defaultCreateDTO);
            PatchUserDTO patchData = new PatchUserDTO(
                    "Johnny",
                    "Silverhand",
                    "new.email@example.com",
                    "newPassword123"
            );

            User patched = userService.patch(original.getPublicId(), patchData);

            assertThat(patched.getFirstName()).isEqualTo("Johnny");
            assertThat(patched.getLastName()).isEqualTo("Silverhand");
            assertThat(patched.getEmail()).isEqualTo("new.email@example.com");
            assertThat(passwordEncoder.matches("newPassword123", patched.getPasswordHash())).isTrue();

            User fromDb = userRepository.findByPublicId(original.getPublicId()).get();
            assertThat(fromDb.getFirstName()).isEqualTo("Johnny");
        }

        @Test
        @DisplayName("Should ignore nulls for firstName, email and password (Partial Update)")
        void shouldIgnoreNullsForMostFields() {
            User original = userService.create(defaultCreateDTO);
            String originalHash = original.getPasswordHash();

            PatchUserDTO patchData = new PatchUserDTO(
                    null,
                    original.getLastName(),
                    null,
                    null
            );

            User patched = userService.patch(original.getPublicId(), patchData);

            assertThat(patched.getFirstName()).isEqualTo(original.getFirstName());
            assertThat(patched.getEmail()).isEqualTo(original.getEmail());
            assertThat(patched.getPasswordHash()).isEqualTo(originalHash);
        }

        @Test
        @DisplayName("Should ERASE lastName if data is null")
        void shouldEraseLastName_WhenNullInDto() {
            User original = userService.create(defaultCreateDTO);

            PatchUserDTO patchData = new PatchUserDTO(
                    "NewName",
                    null,
                    original.getEmail(),
                    null
            );

            User patched = userService.patch(original.getPublicId(), patchData);

            assertThat(patched.getLastName()).isNull();

            User fromDb = userRepository.findByPublicId(original.getPublicId()).get();
            assertThat(fromDb.getLastName()).isNull();
        }

        @Test
        @DisplayName("Should throw exception if new email is already taken by another user")
        void shouldThrowException_WhenEmailTaken() {
            User user1 = userService.create(defaultCreateDTO);
            userService.create(new CreateUserDTO("A", "B","other@test.com", "123"));

            PatchUserDTO patchData = new PatchUserDTO(
                    null, null, "other@test.com", null
            );

            assertThatThrownBy(() -> userService.patch(user1.getPublicId(), patchData))
                    .isInstanceOf(EmailInUseException.class);
        }

        @Test
        @DisplayName("Should allow patching with the SAME email (Self-update)")
        void shouldAllowSameEmail() {
            User original = userService.create(defaultCreateDTO);
            PatchUserDTO patchData = new PatchUserDTO(
                    "NewName", null, original.getEmail(), null
            );

            User patched = userService.patch(original.getPublicId(), patchData);

            assertThat(patched.getEmail()).isEqualTo(original.getEmail());
        }

        @Test
        @DisplayName("Should NOT re-hash password if raw password matches existing hash")
        void shouldNotRehash_WhenPasswordIsSame() {
            User original = userService.create(defaultCreateDTO);
            String originalHash = original.getPasswordHash();

            PatchUserDTO patchData = new PatchUserDTO(
                    null, null, original.getEmail(), defaultCreateDTO.password()
            );

            User patched = userService.patch(original.getPublicId(), patchData);

            assertThat(patched.getPasswordHash()).isEqualTo(originalHash);
        }

        @Test
        @DisplayName("Should throw exception if user ID does not exist")
        void shouldThrowException_WhenUserNotFound() {
            PatchUserDTO patchData = new PatchUserDTO("A", "B", "c@c.com", "123");

            assertThatThrownBy(() -> userService.patch(UUID.randomUUID(), patchData))
                    .isInstanceOf(UserNotExistsException.class);
        }

        @Test
        @DisplayName("Should not change first name when new name is equal old name")
        void shouldChangeName() {
            PatchUserDTO patchData = new PatchUserDTO("John", null, null, null);

            User original = userService.create(defaultCreateDTO);

            User patched = userService.patch(original.getPublicId(), patchData);

            assertThat(patched.getFirstName()).isEqualTo(patchData.firstName());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete existing user")
        void shouldDeleteUser() {
            User user = userService.create(defaultCreateDTO);
            UUID publicId = user.getPublicId();

            userService.delete(publicId);

            assertThat(userRepository.findByPublicId(publicId)).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowException_WhenDeletingNonExistent() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> userService.delete(randomId))
                    .isInstanceOf(UserNotExistsException.class);
        }
    }
}
