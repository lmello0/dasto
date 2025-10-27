package com.lmello.dasto.user.exceptions;

import lombok.Getter;

@Getter
public class EmailInUseException extends RuntimeException {

    private final String email;

    public EmailInUseException(String email) {
        this.email = email;

        super("Email '" + email + "' is already in use");
    }
}
