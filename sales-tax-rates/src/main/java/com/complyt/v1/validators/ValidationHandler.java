package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidationHandler<T, U extends Validator> {

    @NonNull
    Class<T> validationClass;

    @NonNull
    U validator;

    @NonNull
    DataConflictChecksProvider<T> dataConflictChecksProvider;

    @NonNull
    QueryParamsExtractor<T> queryParamsExtractor;

    private Mono<T> onValidationErrors(Errors errors) {
        return Mono.error(new ObjectNotValidApiException(errors));
    }

    public Mono<T> validateRequest(final ServerRequest serverRequest) {
        return queryParamsExtractor.extract(serverRequest)
                .flatMap(object -> {
                    Errors errors = new BeanPropertyBindingResult(object, validationClass.getName());
                    validator.validate(object, errors);

                    if (errors.getAllErrors().isEmpty()) {
                        return Mono.just(object);
                    } else {
                        return onValidationErrors(errors);
                    }
                });
    }

    public final Mono<T> validate(final ServerRequest serverRequest) {
        return this.validateRequest(serverRequest)
                .flatMap(body -> Flux.fromIterable(serverRequest.pathVariables().keySet())
                        .flatMap(variable -> dataConflictChecksProvider.getPathVariableCheck(variable)
                                .flatMap(check -> check.apply(body, serverRequest)))
                        .concatWith(dataConflictChecksProvider.getBodyConflictCheck().flatMap(check -> check.apply(body)))
                        .all(valid -> valid)
                        .flatMap(allValid -> allValid ? Mono.just(body) :
                                Mono.error(new ConflictedDataApiException())));
    }

}