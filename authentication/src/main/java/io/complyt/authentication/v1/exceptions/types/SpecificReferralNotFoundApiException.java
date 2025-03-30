package io.complyt.authentication.v1.exceptions.types;


import io.complyt.authentication.v1.config.error_messages.GenericErrorMessages;
import lombok.Generated;

@Generated
public class SpecificReferralNotFoundApiException extends ComplytApiException {
    private static final String message = GenericErrorMessages.SPECIFIC_REFERRAL_NOT_FOUND_ERROR;

    public SpecificReferralNotFoundApiException() {
        super(message);
    }
}
