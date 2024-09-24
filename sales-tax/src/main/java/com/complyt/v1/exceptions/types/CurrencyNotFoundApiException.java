package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class CurrencyNotFoundApiException extends ComplytApiException {

    public CurrencyNotFoundApiException() {
        super(GenericErrorMessages.CURRENCY_NOT_FOUND_ERROR);
    }

}