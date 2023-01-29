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
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidationHandler<T, U extends Validator> {

    @NonNull
    Class<T> validationClass;

    @NonNull
    U validator;

    public final Mono<T> validate(final ServerRequest request) {
        return request.bodyToMono(validationClass)
                .flatMap(body -> {
            Errors errors = new BeanPropertyBindingResult(body, validationClass.getName());
            validator.validate(body, errors);

            if (errors.getAllErrors().isEmpty()) {
                return Mono.just(body);
            } else {
                return onValidationErrors(errors);
            }
        });
    }

    private Mono<T> onValidationErrors(@NonNull Errors errors) {
        return Mono.error(new ObjectNotValidApiException(errors));
    }
}