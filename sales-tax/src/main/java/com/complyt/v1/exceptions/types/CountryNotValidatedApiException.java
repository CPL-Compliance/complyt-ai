package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class CountryNotValidatedApiException extends ComplytApiException {

    public CountryNotValidatedApiException() {
        super(GenericErrorMessages.COUNTRY_NOT_VALIDATED);
    }
}