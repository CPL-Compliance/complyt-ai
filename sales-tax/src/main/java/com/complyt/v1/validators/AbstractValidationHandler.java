package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public abstract class AbstractValidationHandler<T, U extends Validator> {

    private final Class<T> validationClass;

    private final U validator;

    public AbstractValidationHandler(Class<T> clazz, U validator) {
        this.validationClass = clazz;
        this.validator = validator;
    }

    abstract protected Mono<ServerResponse> processBody(T validBody, final ServerRequest originalRequest);

    public final Mono<ServerResponse> handleRequest(final ServerRequest request) {
        return request.bodyToMono(this.validationClass).flatMap(body -> {
            Errors errors = new BeanPropertyBindingResult(body, this.validationClass.getName());
            this.validator.validate(body, errors);

            if (errors == null || errors.getAllErrors().isEmpty()) {
                return processBody(body, request);
            } else {
                return onValidationErrors(errors);
            }
        });
    }

    protected Mono<ServerResponse> onValidationErrors(@NonNull Errors errors) {
        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.error(new ObjectNotValidApiException(errors)), validationClass);
    }
}