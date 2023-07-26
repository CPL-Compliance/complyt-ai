package com.complyt.v1.config.error_messages;

import com.complyt.v1.api_info.FieldsDescriptions;

public interface DtoErrorMessages {

    String NOT_NULL_ERROR = "may not be null";
    String CONFLICTED_WITH_URL_ERROR = "in body and path should be identical";
    String STATE_CONFLICTED_WITH_URL_ERROR = "in path should be same as the name or abbreviation in body";
    String LIST_NOT_EMPTY_ERROR = "list cannot be empty";

    String DATE_FORMAT_ERROR = "is in an illegal format - " +
            "For date/time fields please provide a " + FieldsDescriptions.TIMESTAMP_FORMAT;

}
