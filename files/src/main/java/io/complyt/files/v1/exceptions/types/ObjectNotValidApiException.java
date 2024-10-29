package io.complyt.files.v1.exceptions.types;

import io.complyt.files.annotations.Generated;
import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;

@Generated
public class ObjectNotValidApiException extends ComplytApiException {

    private static final String message = "The requested operation failed because the request is not valid.";

    public ObjectNotValidApiException(@NonNull Errors errors) {
        super(errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString());
    }

    public ObjectNotValidApiException() {
        super(message);
    }
}