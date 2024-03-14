package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class InvalidPatchFieldException extends ComplytApiException {
    public InvalidPatchFieldException() {
        super(GenericErrorMessages.INVALID_PATCH_FIELD_EXCEPTION);
    }
}
