package com.lmello.dasto.user.exceptions;

public class UserNotExistsException extends RuntimeException {
    public UserNotExistsException() {
        super("User not found");
    }
}
