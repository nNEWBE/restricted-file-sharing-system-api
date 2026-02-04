package com.example.restricted_file_sharing_system.exception;

public class InvalidVerificationTokenException extends RuntimeException {

    public InvalidVerificationTokenException(String message) {
        super(message);
    }

    public InvalidVerificationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
