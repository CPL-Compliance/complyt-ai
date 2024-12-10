package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class MissingBodyApiException extends ComplytApiException {

    public MissingBodyApiException() { super(GenericErrorMessages.MISSING_BODY_ERROR); }
}