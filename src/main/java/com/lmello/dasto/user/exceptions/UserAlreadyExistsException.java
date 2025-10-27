package com.lmello.dasto.user.exceptions;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String email;

    public UserAlreadyExistsException(String email) {
        this.email = email;

        super("Email '" + email + "' is already registered");
    }
}
