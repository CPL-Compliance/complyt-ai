package io.complyt.v1.config.error_messages;

public interface DtoErrorMessages {
    String NON_PARTIAL_ERROR_SUFFIX = "in a non partial address"; // Appended to other errors messages
    String ABBREVIATION_DOES_NOT_EXIST = "state abbreviation does not exist";
    String ZIP_NOT_IN_FORMAT = "The ZIP code you provided is invalid. Please enter a valid ZIP code in either the 5-digit format (e.g., 12345) or the 9-digit format with a hyphen (e.g., 12345-6789).";
}

