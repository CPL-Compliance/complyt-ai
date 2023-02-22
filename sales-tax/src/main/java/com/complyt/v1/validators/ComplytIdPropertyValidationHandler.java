package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.models.properties.ComplytIdPropertyDto;
import lombok.NonNull;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;


public class ComplytIdPropertyValidationHandler<T extends ComplytIdPropertyDto, U extends Validator> extends ValidationHandler<T, U> {
    public ComplytIdPropertyValidationHandler(@NonNull Class<T> validationClass, @NonNull U validator) {
        super(validationClass, validator);
    }

    @Override
    public Mono<T> validate(final ServerRequest serverRequest) {
        UUID complytId = UUID.fromString(serverRequest.pathVariable("complytId"));

        return this.validateRequestBody(serverRequest).flatMap(resource ->
                complytId.equals(resource.complytId()) ?
                        Mono.just(resource) : Mono.error(new ConflictedDataApiException()));
    }
}
