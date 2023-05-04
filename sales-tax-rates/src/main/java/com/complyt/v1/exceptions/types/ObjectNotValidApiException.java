package com.complyt.v1.exceptions.types;


import com.complyt.annotations.Generated;
import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;

@Generated
public class ObjectNotValidApiException extends ComplytApiException {
    public ObjectNotValidApiException(@NonNull Errors errors) {
        super(errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString());
    }
}