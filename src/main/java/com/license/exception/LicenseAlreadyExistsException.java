package com.license.exception;

public class LicenseAlreadyExistsException extends RuntimeException {

     public LicenseAlreadyExistsException(String message) {
        super(message);
    }

    public LicenseAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
