package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class ObjectNotFoundApiException extends ComplytApiException {

    public ObjectNotFoundApiException() {
        super(GenericErrorMessages.NOT_FOUND_ERROR);
    }

}