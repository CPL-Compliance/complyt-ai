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

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatchingHandler<T, U extends Validator> {

    @NonNull
    Patcher<T> patcher;

    @NonNull
    Class<T> validationClass;

    @NonNull
    U validator;

    public Mono<T> patch(ServerRequest serverRequest, T existingObject) {

        return (Mono<T>) serverRequest.bodyToMono(Map.class)
                .map(patchValues -> patcher.patch(existingObject, patchValues))
                .flatMap(patchedObject -> {
                    Errors errors = new BeanPropertyBindingResult(patchedObject, validationClass.getName());
                    validator.validate(patchedObject, errors);

                    if (errors.getAllErrors().isEmpty()) {
                        return Mono.just(patchedObject);
                    } else {
                        return Mono.error(new ObjectNotValidApiException(errors));
                    }
                });
    }

}