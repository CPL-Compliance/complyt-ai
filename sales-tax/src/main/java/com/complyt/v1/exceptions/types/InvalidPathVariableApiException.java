package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;

@Generated
public class InvalidPathVariableApiException extends ComplytApiException {
    private static final String message = "One or more path variable in the target url is invalid";

    public InvalidPathVariableApiException() {
        super(message);
    }
}