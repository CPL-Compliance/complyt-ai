package com.complyt.v1.exceptions.types;

import com.complyt.annotations.Generated;
import com.complyt.v1.config.error_messages.GenericErrorMessages;

@Generated
public class ZipCodeNotFoundApiException extends ComplytApiException {

    public ZipCodeNotFoundApiException() {
        super(GenericErrorMessages.ZIP_CODE_NOT_FOUND_ERROR);
    }

}