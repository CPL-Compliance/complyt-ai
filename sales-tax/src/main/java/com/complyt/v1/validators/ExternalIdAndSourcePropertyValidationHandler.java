package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.models.properties.ComplytIdPropertyDto;
import com.complyt.v1.models.properties.ExternalIdPropertyDto;
import com.complyt.v1.models.properties.SourcePropertyDto;
import lombok.NonNull;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;


public class ExternalIdAndSourcePropertyValidationHandler<T extends SourcePropertyDto & ExternalIdPropertyDto, U extends Validator> extends ValidationHandler<T, U> {
    public ExternalIdAndSourcePropertyValidationHandler(@NonNull Class<T> validationClass, @NonNull U validator) {
        super(validationClass, validator);
    }

    @Override
    public Mono<T> validate(final ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");

        return this.validateRequestBody(serverRequest).flatMap(resource ->
                source.equals(resource.source()) && externalId.equals(resource.externalId()) ?
                        Mono.just(resource) : Mono.error(new ConflictedDataApiException()));
    }
}
