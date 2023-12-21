package com.complyt.business.exceptions;

import com.complyt.annotations.Generated;

@Generated
public class ComplytSalesTaxRatesException extends Exception {
    public ComplytSalesTaxRatesException() {
        super("ComplytSalesTaxRatesException: Sales-tax-rates error occurred");
    }

    public ComplytSalesTaxRatesException(String message) {
        super(message);
    }

    public ComplytSalesTaxRatesException(String message, Throwable cause) {
        super(message, cause);
    }
}