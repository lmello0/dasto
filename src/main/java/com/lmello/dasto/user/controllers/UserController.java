package com.lmello.dasto.user.controllers;

import com.lmello.dasto.user.dto.input.CreateUserDTO;
import com.lmello.dasto.user.dto.input.PatchUserDTO;
import com.lmello.dasto.user.dto.output.UserResponse;
import com.lmello.dasto.user.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
        Page<UserResponse> page = userService.getAllUsers(pageable);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String publicId) {
        UserResponse user = userService.getUserById(publicId);

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserDTO data) {
        UserResponse user = userService.create(data);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(user.publicId())
                .toUri();

        return ResponseEntity.created(location).body(user);
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable String publicId, @Valid @RequestBody PatchUserDTO data) {
        UserResponse user = userService.patch(publicId, data);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String publicId) {
        userService.delete(publicId);

        return ResponseEntity.noContent().build();
    }
}
