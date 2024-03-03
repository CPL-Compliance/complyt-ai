package io.complyt.authentication.v1.exceptions.types;


import io.complyt.authentication.annotations.Generated;
import io.complyt.authentication.v1.config.error_messages.GenericErrorMessages;
import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;

@Generated
public class ObjectNotValidApiException extends ComplytApiException {
    private static final String message = GenericErrorMessages.UNAUTHORIZED_ERROR;
    public ObjectNotValidApiException(@NonNull Errors errors) {
        super(errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString());
    }

    public ObjectNotValidApiException(@NonNull String error) {
        super(error);
    }

    public ObjectNotValidApiException() {
        super(message);
    }
}