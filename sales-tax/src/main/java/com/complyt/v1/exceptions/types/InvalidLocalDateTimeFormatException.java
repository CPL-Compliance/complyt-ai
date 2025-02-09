package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class InvalidLocalDateTimeFormatException extends ComplytApiException {

    public InvalidLocalDateTimeFormatException(String fieldName) {
        super("Error parsing field: " + fieldName + ". " + GenericErrorMessages.INVALID_DATE_TIME_FORMAT_EXCEPTION);
    }
}
