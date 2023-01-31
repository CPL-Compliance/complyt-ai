package io.complyt.files.v1.exceptions.types;


import io.complyt.files.annotations.Generated;

@Generated
public class ObjectNotFoundApiException extends ComplytApiException {
    private static final String message = "The requested operation failed because a resource associated with the request could not be found.";

    public ObjectNotFoundApiException() {
        super(message);
    }
}