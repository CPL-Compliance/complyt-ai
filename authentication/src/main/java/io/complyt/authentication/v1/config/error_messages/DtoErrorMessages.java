package io.complyt.authentication.v1.config.error_messages;


public interface DtoErrorMessages {
    String NOT_NULL_ERROR = "may not be null";
    String NOT_NULL_BLANK = "may not be blank";

    String TENANT_ID_FORMAT_ERROR = "tenantId must not be null or blank";
    String NOT_NULL_OR_BLANK_ERROR = "must not be null or blank";

}
