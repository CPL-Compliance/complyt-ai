package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;

@Generated
public class ConflictedDataApiException extends ComplytApiException {
    private static final String message = "The requested operation failed because there was an unresolvable conflict between two or more inputs";

    public ConflictedDataApiException() {
        super(message);
    }
}