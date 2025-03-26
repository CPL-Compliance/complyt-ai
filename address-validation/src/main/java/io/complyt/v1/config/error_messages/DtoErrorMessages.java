package io.complyt.v1.config.error_messages;

public interface DtoErrorMessages {
    String ABBREVIATION_DOES_NOT_EXIST = "state abbreviation does not exist";
    String ZIP_NOT_IN_FORMAT = "The ZIP code you provided is invalid. Please enter a valid ZIP code in either the 5-digit format (e.g., 12345) or the 9-digit format with a hyphen (e.g., 12345-6789)";
    String STATE_NOT_RECOGNIZED_USA = "The state in the provided USA address is not recognized. Please verify and provide a valid state";
    String NON_PARTIAL_ERROR_SUFFIX = "in a non-partial address"; // Appended to other errors messages
    String PARTIAL_ERROR_SUFFIX = "in a partial address"; // Appended to other errors messages
}

