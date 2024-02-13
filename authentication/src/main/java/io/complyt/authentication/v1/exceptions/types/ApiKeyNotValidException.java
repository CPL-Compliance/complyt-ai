package io.complyt.authentication.v1.exceptions.types;

import io.complyt.authentication.v1.config.error_messages.GenericErrorMessages;
import lombok.Generated;

@Generated
public class ApiKeyNotValidException extends ComplytApiException{

    private static final String message = GenericErrorMessages.UNAUTHORIZED_ERROR;

    public ApiKeyNotValidException() {
        super(message);
    }
}


