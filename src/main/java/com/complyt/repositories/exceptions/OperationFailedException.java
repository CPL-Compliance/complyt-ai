package com.complyt.repositories.exceptions;

public class OperationFailedException extends RuntimeException {
    public OperationFailedException() {
        super();
    }
    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    public OperationFailedException(String message) {
        super(message);
    }
    public OperationFailedException(Throwable cause) {
        super(cause);
    }
}
