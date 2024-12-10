package io.complyt.utils.exceptions.types;

import io.complyt.v1.config.error_messages.GenericErrorMessages;

public class ZipCodeMismatchException extends ComplytException {

    public ZipCodeMismatchException(String providedZip, String correctZip, String address) {
        super(String.format(GenericErrorMessages.ZIP_CODE_MISMATCH, providedZip, correctZip, address));
    }

}
