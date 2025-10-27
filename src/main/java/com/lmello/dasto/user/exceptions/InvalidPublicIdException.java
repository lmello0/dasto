package com.lmello.dasto.user.exceptions;

import lombok.Getter;

@Getter
public class InvalidPublicIdException extends RuntimeException {

    private final String publicId;

    public InvalidPublicIdException(String publicId) {
        this.publicId = publicId;

        super("Public ID is invalid");
    }
}
