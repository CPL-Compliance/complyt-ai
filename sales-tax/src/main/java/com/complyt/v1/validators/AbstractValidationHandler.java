package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.models.customer.CustomerDto;
import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractValidationHandler<T, U extends Validator> {

    private final Class<T> validationClass;

    private final U validator;

    protected AbstractValidationHandler(Class<T> clazz, U validator) {
        this.validationClass = clazz;
        this.validator = validator;
    }

    abstract protected Mono<ServerResponse> processBody(T validBody, final ServerRequest originalRequest);

    public final Mono<ServerResponse> handleRequest(final ServerRequest request) {
        return request.bodyToMono(this.validationClass)
                .flatMap(body -> {
                    Errors errors = new BeanPropertyBindingResult(body, this.validationClass.getName());
                    this.validator.validate(body, errors);

                    if (errors == null || errors.getAllErrors().isEmpty()) {
                        return processBody(body, request);
                    } else {
                        return onValidationErrors(errors, body, request);
                    }
                });
    }

    protected Mono<ServerResponse> onValidationErrors(@NonNull Errors errors, T invalidBody, final ServerRequest request) {
        List<String> strings = errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        return ServerResponse
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.error(new ObjectNotValidApiException(strings.toString())), validationClass);
    }
}