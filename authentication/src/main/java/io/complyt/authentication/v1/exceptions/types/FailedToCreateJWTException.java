package io.complyt.authentication.v1.exceptions.types;

import io.complyt.authentication.v1.config.error_messages.GenericErrorMessages;
import lombok.Generated;

@Generated
public class FailedToCreateJWTException extends ComplytApiException{

    private static final String message = GenericErrorMessages.FAILED_TO_CREATE_TOKEN;

    public FailedToCreateJWTException() {
        super(message);
    }
}


