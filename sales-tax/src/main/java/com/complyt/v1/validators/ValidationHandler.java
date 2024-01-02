package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.MissingBodyApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.exceptions.types.PathVariableErrorException;
import com.complyt.v1.validators.custom_body.CustomBodyExtractor;
import com.complyt.v1.validators.param_checker.PathVariableChecksProvider;
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
    CustomBodyExtractor<T> customBodyExtractor;

    @NonNull
    PathVariableChecksProvider pathVariableChecksProvider;

    private Mono<T> onValidationErrors(Errors errors) {
        return Mono.error(new ObjectNotValidApiException(errors));
    }

    private Mono<T> validateRequestBody(final ServerRequest request) {
        return customBodyExtractor.extract(request)
                .switchIfEmpty(request.bodyToMono(validationClass))
                .flatMap(body -> {
                    Errors errors = new BeanPropertyBindingResult(body, validationClass.getName());
                    validator.validate(body, errors);

                    if (errors.getAllErrors().isEmpty()) {
                        return Mono.just(body);
                    } else {
                        return onValidationErrors(errors);
                    }
                })
                .switchIfEmpty(Mono.error(new MissingBodyApiException()));
    }

    public Mono<T> handle(final ServerRequest serverRequest) {
        return validatePathVariable(serverRequest)
                .then(dataConflictChecksProvider.isBodyEmpty(serverRequest)
                                .flatMap(isEmpty -> isEmpty ? serverRequest.bodyToMono(validationClass) : validate(serverRequest))
                );
    }


    public final Mono<T> validate(final ServerRequest serverRequest) {
        return this.validateRequestBody(serverRequest)
                .flatMap(body -> Flux.fromIterable(serverRequest.pathVariables().keySet())
                        .flatMap(variable -> dataConflictChecksProvider.getPathVariableCheck(variable)
                                .flatMap(check -> check.apply(body, serverRequest)))
                        .concatWith(dataConflictChecksProvider.getBodyConflictCheck()
                                .flatMapMany(check -> check.apply(body)))
                        .collectList()
                        .flatMap(errorList -> errorList.isEmpty() ? Mono.just(body) :
                                Mono.error(new ConflictedDataApiException(errorList))));
    }

    public final Mono<ServerRequest> validatePathVariable(final ServerRequest serverRequest) {
        return Flux.fromIterable(serverRequest.pathVariables().keySet())
                .flatMap(pathVariableChecksProvider::getPathVariableCheck)
                .flatMap(check -> check.apply(serverRequest))
                .collectList()
                .flatMap(errorList -> errorList.isEmpty() ? Mono.just(serverRequest) :
                        Mono.error(new PathVariableErrorException(errorList)));
    }


}