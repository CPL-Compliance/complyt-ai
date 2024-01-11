package com.complyt.v1.config.error_messages;

import com.complyt.v1.api_info.FieldsDescriptions;

public interface DtoErrorMessages {

    String NOT_NULL_ERROR = "may not be null";

    String CONFLICTED_WITH_URL_ERROR = "in body and path should be identical";

    String STATE_CONFLICTED_WITH_URL_ERROR = "in path should be same as the name or abbreviation in body";

    String LIST_NOT_EMPTY_ERROR = "list cannot be empty";

    String COMPLYT_ID_CANNOT_BE_UPDATED_ERROR = "complytId cannot be changed in an update";

    String COMPLYT_ID_IN_A_NEW_RECORD_ERROR = "new record cannot have a complytId field";

    String NON_PARTIAL_ERROR_SUFFIX = "in a non partial address"; // Appended to other errors messages

    String ISO8601_FORMAT_ERROR = "is in an illegal format - " +
                                  "For date/time fields please provide a " + FieldsDescriptions.TIMESTAMP_FORMAT;

    String LOCALDATE_FORMAT_ERROR = "must be in the format yyyy-mm-dd";

    String TOTAL_AMOUNT_AFTER_DISCOUNT_IS_BELOW_ZERO = "The transaction total amount after discount is below 0";
}
