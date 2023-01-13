package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;

@Generated
public class ObjectNotValidApiException extends ComplytApiException {
//    private static final String message = "The requested operation failed because a resource associated with the request could not be found.";
    public ObjectNotValidApiException(String message) {
        super(message);
    }
}