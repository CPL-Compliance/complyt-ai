package io.complyt.authentication.v1.config.error_messages;

public interface GenericErrorMessages {

    String INTERNAL_SERVER_ERROR = "The request failed due to an internal error. Please contact support@complyt.io if this continues";
    String NOT_FOUND_ERROR = "The requested operation failed because a resource associated with the request could not be found.";

    String UNSUPPORTED_MEDIA_TYPE = "415 Unsupported Media Type";

    String UNAUTHORIZED_ERROR = "401 Unauthorized";
}
