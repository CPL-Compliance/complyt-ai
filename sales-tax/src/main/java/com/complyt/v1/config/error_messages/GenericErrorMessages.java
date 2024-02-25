package com.complyt.v1.config.error_messages;

public interface GenericErrorMessages {

    String DATA_CONFLICT_ERROR = "The requested operation failed because there was an unresolvable conflict between two or more inputs";

    String INTERNAL_SERVER_ERROR = "The request failed due to an internal error. Please contact support@complyt.io if this continues";
    String NOT_FOUND_ERROR = "The requested operation failed because a resource associated with the request could not be found.";

    String MISSING_BODY_ERROR = "The requested operation failed because no request body has been provided";
    String UNSUPPORTED_MEDIA_TYPE = "415 Unsupported Media Type";
    String TENANT_ID_FORMAT = "invalid tenantId provided. please provide a valid parameter";

    String MAX_256_ERROR = "should be up to 256 characters maximum";

    String NOT_NULL = "may not be null";

    String CONFLICTED_WITH_URL_ERROR = "in body and path should be identical";
}

