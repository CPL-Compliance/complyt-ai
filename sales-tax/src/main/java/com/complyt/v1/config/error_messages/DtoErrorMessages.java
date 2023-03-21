package com.complyt.v1.config.error_messages;

public interface DtoErrorMessages {

    String not_null_error = " may not be null";

    String list_not_empty_error = " list cannot be empty";

    String date_format_error = " is in an illegal format - " +
            "For date/time fields please provide a valid ISO8601 format. " +
            "Supported formats are 'YYYY-MM-DD'/ 'YYYY-MM-DDTHH:mm:ssZ'/ and 'YYYY-MM-DDTHH:mm:ss±hh:mm' " +
            "(with a valid time zone offset).";

}
