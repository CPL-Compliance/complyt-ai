package com.complyt.v1.config.error_messages;

import com.complyt.v1.api_info.sales_tax_rates.FieldsDescriptions;

// Appended to other errors messages
public interface DtoErrorMessages {
    String NON_PARTIAL_ERROR_SUFFIX = "in a non-partial address";

    String PARTIAL_ERROR_SUFFIX = "in a partial address"; // Appended to other errors messages

    String STATE_NOT_RECOGNIZED_USA = "The state in the provided USA address is not recognized. Please verify and provide a valid state";

    String ZIP_NOT_IN_FORMAT = "Zip format is incorrect. The correct format is mandatory 5 digits and optional dash and and additional 4 digits. example: 12345 or 12345-6789";

    String NOT_NULL_ERROR = "may not be null";

    String NOT_BLANK_ERROR = "may not be blank";

    String DATE_FORMAT_ERROR = "is in an illegal format - " +
            "For date/time fields please provide a " + FieldsDescriptions.TIMESTAMP_FORMAT;

    String NOT_NEGATIVE_ERROR = "can not be a negative number";

    String DECIMAL_MAX_1_ERROR = "'s maximum value is 1.0";

    String COMPLYT_ID_CANNOT_BE_UPDATED_ERROR = "complytId cannot be changed in an update";

    String COMPLYT_ID_IN_A_NEW_RECORD_ERROR = "new record cannot have a complytId field";

    String ISO8601_FORMAT_ERROR = "is in an illegal format - " +
            "For date/time fields please provide a " + FieldsDescriptions.TIMESTAMP_FORMAT;

    String INVALID_COUNTRY_ERROR =  "Invalid country provided. Only US addresses are supported. Please provide a valid US address";
}
