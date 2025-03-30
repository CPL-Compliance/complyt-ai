package io.complyt.authentication.v1.config.error_messages;

public interface GenericErrorMessages {

    String INTERNAL_SERVER_ERROR = "The request failed due to an internal error. Please contact support@complyt.io if this continues";
    String NOT_FOUND_ERROR = "The requested operation failed because a resource associated with the request could not be found.";
    String REFERRALS_NOT_FOUND_ERROR = "The referrals associated with the partner could not be found.";
    String SPECIFIC_REFERRAL_NOT_FOUND_ERROR = "The referral associated with the request could not be found.";
    String PARTNERSHIP_NOT_FOUND_ERROR = "The partner associated with the request could not be found.";

    String UNSUPPORTED_MEDIA_TYPE = "415 Unsupported Media Type";

    String TENANT_NOT_SUPPORTED = "The tenant associated with the request is not supported for this operation.";
    String UNAUTHORIZED_ERROR = "401 Unauthorized";
    String FAILED_TO_CREATE_TOKEN = "Failed to create a new token";
}
