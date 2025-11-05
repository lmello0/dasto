package com.lmello.dasto.user;

import com.lmello.dasto.user.dto.input.CreateUserDTO;
import com.lmello.dasto.user.dto.input.PatchUserDTO;
import com.lmello.dasto.user.dto.output.UserDetailResponse;
import com.lmello.dasto.user.dto.output.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
        Page<User> rawPage = userService.getAllUsers(pageable);
        Page<UserResponse> page = rawPage.map(UserResponse::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable UUID publicId) {
        User u = userService.getUserById(publicId);
        UserDetailResponse user = new UserDetailResponse(u);

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDetailResponse> createUser(@Valid @RequestBody CreateUserDTO data) {
        User u = userService.create(data);
        UserDetailResponse user = new UserDetailResponse(u);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(user.publicId())
                .toUri();

        return ResponseEntity.created(location).body(user);
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable UUID publicId, @Valid @RequestBody PatchUserDTO data) {
        User u = userService.patch(publicId, data);
        UserResponse user = new UserResponse(u);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID publicId) {
        userService.delete(publicId);

        return ResponseEntity.noContent().build();
    }
}
