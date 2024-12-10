package com.complyt.v1.config.error_messages;

public interface GenericErrorMessages {

    String INTERNAL_SERVER_ERROR = "The request failed due to an internal error. Please contact support@complyt.io if this continues";
    String NOT_FOUND_ERROR = "The requested operation failed because a resource associated with the request could not be found.";
    String MISSING_BODY_ERROR = "The requested operation failed because no request body has been provided";
    String ACCESSING_FUNCTION_IN_WRONG_PROFILE = "The function accessed could not be run in the current active profile";
}
