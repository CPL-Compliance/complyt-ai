package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class CustomerNotFoundApiException extends ComplytApiException {

    public CustomerNotFoundApiException() {
        super(GenericErrorMessages.CUSTOMER_NOT_FOUND);
    }

}