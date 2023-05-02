package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidationHandler<T, U extends Validator> {

    @NonNull
    Class<T> validationClass;

    @NonNull
    U validator;

    private Mono<T> onValidationErrors(Errors errors) {
        return Mono.error(new ObjectNotValidApiException(errors));
    }

    public Mono<T> validate(final T object) {
        Errors errors = new BeanPropertyBindingResult(object, validationClass.getName());
        validator.validate(object, errors);

        if (errors.getAllErrors().isEmpty()) {
            return Mono.just(object);
        } else {
            return onValidationErrors(errors);
        }
    }

}