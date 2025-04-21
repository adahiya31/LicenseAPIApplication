package com.license.exception;

public class LicenseAlreadyExistsException extends RuntimeException {

    // Constructor to pass custom message
    public LicenseAlreadyExistsException(String message) {
        super(message);
    }

    // Optionally, you can add other constructors if you need to pass more details (like a cause)
    public LicenseAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
