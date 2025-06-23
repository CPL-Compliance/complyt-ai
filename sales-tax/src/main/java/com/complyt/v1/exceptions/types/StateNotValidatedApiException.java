package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class StateNotValidatedApiException extends ComplytApiException {

    public StateNotValidatedApiException() {
        super(GenericErrorMessages.STATE_NOT_VALIDATED);
    }
}