package io.complyt.authentication.v1.exceptions.types;

import io.complyt.authentication.v1.config.error_messages.GenericErrorMessages;
import lombok.Generated;

@Generated
public class TenantNotSupportedException extends ComplytApiException{

    private static final String message = GenericErrorMessages.TENANT_NOT_SUPPORTED;

    public TenantNotSupportedException() {
        super(message);
    }
}


