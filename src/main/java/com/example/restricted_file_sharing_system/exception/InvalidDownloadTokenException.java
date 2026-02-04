package com.example.restricted_file_sharing_system.exception;

public class InvalidDownloadTokenException extends RuntimeException {

    public InvalidDownloadTokenException(String message) {
        super(message);
    }

    public InvalidDownloadTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
