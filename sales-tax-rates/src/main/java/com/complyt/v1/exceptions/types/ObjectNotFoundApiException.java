package com.complyt.v1.exceptions.types;


import com.complyt.v1.config.error_messages.GenericErrorMessages;
import lombok.Generated;

@Generated
public class ObjectNotFoundApiException extends ComplytApiException {
    private static final String message = GenericErrorMessages.NOT_FOUND_ERROR;

    public ObjectNotFoundApiException() {
        super(message);
    }
}
