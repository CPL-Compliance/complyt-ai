package io.complyt.v1.config.error_messages;

public interface GenericErrorMessages {
    String ADDRESS_VALIDATION_ERROR = "ERR-ADDR-001";
    String ZIP_CODE_MISMATCH_ERROR = "ERR-ADDR-002";

    String INTERNAL_SERVER_ERROR = "The request failed due to an internal error. Please contact support@complyt.io if this continues";

    String ADDRESS_NOT_VALID = ADDRESS_VALIDATION_ERROR + ": The address could not be validated. Please check that the street, city, state, and ZIP are correct and properly formatted.";

    String ZIP_CODE_MISMATCH =  ZIP_CODE_MISMATCH_ERROR + ": The ZIP code you provided (%s) does not match the address entered. Did you mean ZIP code %s for the address '%s'?";

    String STATE_CODE_MISMATCH =  ZIP_CODE_MISMATCH_ERROR + ": The state you provided (%s) does not match the address entered. The expected state for this address is'%s'.";

    String ZIP_FORMAT_INVALID = "The ZIP code you provided is invalid. Please enter a valid ZIP code in either the 5-digit format (e.g., 12345) or the 9-digit format with a hyphen (e.g., 12345-6789).";

}
