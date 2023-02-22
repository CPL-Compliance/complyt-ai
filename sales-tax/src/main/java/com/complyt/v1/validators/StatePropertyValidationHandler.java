package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.models.properties.StatePropertyDto;
import lombok.NonNull;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;


public class StatePropertyValidationHandler<T extends StatePropertyDto, U extends Validator> extends ValidationHandler<T, U> {
    public StatePropertyValidationHandler(@NonNull Class<T> validationClass, @NonNull U validator) {
        super(validationClass, validator);
    }

    @Override
    public Mono<T> validate(final ServerRequest serverRequest) {
        String state = serverRequest.pathVariable("state");

        return this.validateRequestBody(serverRequest).flatMap(resource ->
                state.equals(resource.state().name()) || state.equals(resource.state().abbreviation()) ?
                        Mono.just(resource) : Mono.error(new ConflictedDataApiException()));
    }
}
