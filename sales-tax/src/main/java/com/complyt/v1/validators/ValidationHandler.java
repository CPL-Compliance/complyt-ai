package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.*;
import com.complyt.v1.validators.custom_body.CustomBodyExtractor;
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
    ParameterChecksProvider pathVariableChecksProvider;

    @NonNull
    ParameterChecksProvider queryParamChecksProvider;

    @NonNull
    ShouldCallValidate shouldCallValidate;

    private Mono<T> validateRequestBody(final ServerRequest request) {
        return customBodyExtractor.extract(request)
                .switchIfEmpty(request.bodyToMono(validationClass))
                .flatMap(body -> {
                    Errors errors = new BeanPropertyBindingResult(body, validationClass.getName());
                    validator.validate(body, errors);

                    if (errors.getAllErrors().isEmpty()) {
                        return Mono.just(body);
                    } else {
                        return Mono.error(new ObjectNotValidApiException(errors));
                    }
                })
                .switchIfEmpty(Mono.error(new MissingBodyApiException()));
    }

    public Mono<T> handle(final ServerRequest serverRequest) {

        return validatePathVariable(serverRequest)
                .then(validateQueryParam(serverRequest))
                .then(Mono.defer(() -> shouldCallValidate.apply(serverRequest) ? validate(serverRequest) : Mono.empty()));
    }


    private Mono<T> validate(final ServerRequest serverRequest) {
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

    private Mono<Boolean> validateQueryParam(final ServerRequest serverRequest) {
        return queryParamChecksProvider.doesParamExist(serverRequest)
                .then(Flux.fromIterable(serverRequest.queryParams().entrySet())
                        .flatMap(entry -> Flux.fromIterable(entry.getValue())
                                .flatMap(paramValue -> queryParamChecksProvider.getFunctionCheck(entry.getKey())
                                        .flatMapMany(check -> check.apply(paramValue))))
                        .collectList()
                        .flatMap(errorList -> errorList.isEmpty() ? Mono.just(true) :
                                Mono.error(new QueryParamErrorException(errorList))))
                .switchIfEmpty(Mono.just(true));
    }


    private Mono<Boolean> validatePathVariable(final ServerRequest serverRequest) {
        return Flux.fromIterable(serverRequest.pathVariables().entrySet())
                .flatMapSequential(entry -> pathVariableChecksProvider.getFunctionCheck(entry.getKey())
                        .flatMapMany(check -> check.apply(entry.getValue())))
                .collectList()
                .flatMap(errorList -> errorList.isEmpty() ? Mono.just(true) :
                        Mono.error(new PathVariableErrorException(errorList)));
    }


}