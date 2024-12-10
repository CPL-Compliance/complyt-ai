package com.complyt.v1.config.error_messages;

import com.complyt.v1.api_info.sales_tax_rates.FieldsDescriptions;

// Appended to other errors messages
public interface DtoErrorMessages {
    String NON_PARTIAL_ERROR_SUFFIX = "in a non partial address";

    String NOT_NULL_ERROR = "may not be null";

    String NOT_BLANK_ERROR = "may not be blank";

    String DATE_FORMAT_ERROR = "is in an illegal format - " +
            "For date/time fields please provide a " + FieldsDescriptions.TIMESTAMP_FORMAT;

    String NOT_NEGATIVE_ERROR = "can not be a negative number";

    String DECIMAL_MAX_1_ERROR = "'s maximum value is 1.0";

    String COMPLYT_ID_CANNOT_BE_UPDATED_ERROR = "complytId cannot be changed in an update";

    String COMPLYT_ID_IN_A_NEW_RECORD_ERROR = "new record cannot have a complytId field";

    String UNSUPPORTED_STATE = "the state entered is unsupported";

    String ZIP_DIGITS_ARE_MISORDERED = "lowerPlusFourDigits should be either equal or be lower than upperPlusFourDigits";

    String HAS_PLUS_FOUR_ZIP_DIGITS_TRUE_ZIP_DIGITS_MUST_BE_DIFFERENT_THAN_ZERO = "hasPlusFourZipCode is set to true but lower PlusFourDigits and upperPlusFourDigits are equals to 0";

    String HAS_PLUS_FOUR_ZIP_FALSE_PLUS_FOUR_DIGITS_MUST_BE_ZERO = "hasPlusFourZipCode is set to false but lower PlusFourDigits and upperPlusFourDigits are NOT equals to 0";

    String INCORPORATED_ADDRESS_MUST_NOT_HAVE_UNINCORPORATED_CITY = "incorporated address must NOT have unincorporated city";
    String UNINCORPORATED_ADDRESS_MUST_NOT_HAVE_INCORPORATED_CITY = "unincorporated address must NOT have incorporated city";

    String ADDRESS_NOT_VALID = "address could not be validated";

    String STATE_FORMAT_ERROR = "invalid state provided. Please provide a valid state name or abbreviation.\";";

    String LOCALDATE_FORMAT_ERROR = "must be in the format yyyy-mm-dd";

    String ISO8601_FORMAT_ERROR = "is in an illegal format - " +
            "For date/time fields please provide a " + FieldsDescriptions.TIMESTAMP_FORMAT;
}
