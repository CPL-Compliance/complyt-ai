package io.complyt.authentication.business.exceptions;

import io.complyt.authentication.annotations.Generated;

@Generated
public class ComplytAuth0Exception extends Exception {
    public ComplytAuth0Exception() {
        super("ComplytAuth0Exception: An authentication error occurred.");
    }

    public ComplytAuth0Exception(String message) {
        super(message);
    }

    public ComplytAuth0Exception(String message, Throwable cause) {
        super(message, cause);
    }
}