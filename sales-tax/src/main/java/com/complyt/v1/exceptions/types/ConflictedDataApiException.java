package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class ConflictedDataApiException extends ComplytApiException {

    public ConflictedDataApiException() {
        super(GenericErrorMessages.DATA_CONFLICT_ERROR);
    }
}