package com.example.restricted_file_sharing_system.exception;

/**
 * Exception thrown when there's an error storing a file.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
