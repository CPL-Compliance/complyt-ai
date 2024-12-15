package com.complyt.business.exceptions;

import com.complyt.annotations.Generated;

@Generated
public class ComplytAddressValidationException extends Exception {
    public ComplytAddressValidationException() {
        super("ComplytSalesTaxRatesException: Address-Validation error occurred");
    }

    public ComplytAddressValidationException(String message) {
        super(message);
    }

    public ComplytAddressValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}