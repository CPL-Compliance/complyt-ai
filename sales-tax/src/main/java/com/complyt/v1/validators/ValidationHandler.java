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

import java.util.List;
import java.util.Map;
import java.util.Set;


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

    public Mono<T> handle(final ServerRequest serverRequest) {
        return validatePathVariable(serverRequest.pathVariables().entrySet())
                .then(validateQueryParam(serverRequest))
                .then(Mono.defer(() -> shouldCallValidate.apply(serverRequest) ? handleRequestBody(serverRequest) : Mono.empty()));
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

    private Mono<Boolean> validatePathVariable(@NonNull final Set<Map.Entry<String, String>> entrySet) {
        return Flux.fromIterable(entrySet)
                .flatMapSequential(entry -> pathVariableChecksProvider.getFunctionCheck(entry.getKey())
                        .flatMapMany(check -> check.apply(entry.getValue())))
                .collectList()
                .flatMap(errorList -> errorList.isEmpty() ? Mono.just(true) :
                        Mono.error(new PathVariableErrorException(errorList)));
    }

    public Mono<String> validateParam(@NonNull String key, @NonNull String value) {
        return pathVariableChecksProvider.getFunctionCheck(key)
                .flatMap(check -> check.apply(value))
                .flatMap(error -> Mono.error(new PathVariableErrorException(List.of(error))));
    }

    private Mono<T> checkErrorList(T object, List<String> errorList) {
        return errorList.isEmpty() ? Mono.just(object) :
                Mono.error(new ConflictedDataApiException(errorList));
    }

    private Mono<T> handleRequestBody(final ServerRequest serverRequest) {
        return validateRequestBody(serverRequest)
                .flatMap(body -> Flux.fromIterable(serverRequest.pathVariables().keySet())
                        .flatMap(variable -> dataConflictChecksProvider.getPathVariableCheck(variable)
                                .flatMap(check -> check.apply(body, serverRequest)))
                        .concatWith(checkBodyConflicts(body))
                        .collectList()
                        .flatMap(errorList -> checkErrorList(body, errorList)));
    }

    private Mono<T> validateRequestBody(final ServerRequest serverRequest) {
        return customBodyExtractor.extract(serverRequest)
                .switchIfEmpty(serverRequest.bodyToMono(validationClass))
                .flatMap(this::validateBody)
                .switchIfEmpty(Mono.error(new MissingBodyApiException()));
    }

    public Mono<T> handle(@NonNull final T object, @NonNull final Set<Map.Entry<String, String>> entrySet) {
        return validatePathVariable(entrySet)
                .then(validateBody(object))
                .flatMapMany(this::checkBodyConflicts)
                .collectList()
                .flatMap(errorList -> checkErrorList(object, errorList));
    }

    private Mono<T> validateBody(final T object) {
        Errors errors = new BeanPropertyBindingResult(object, validationClass.getName());
        validator.validate(object, errors);

        if (errors.getAllErrors().isEmpty()) {
            return Mono.just(object);
        } else {
            return Mono.error(new ObjectNotValidApiException(errors));
        }
    }

    private Flux<String> checkBodyConflicts(final T body) {
        return dataConflictChecksProvider.getBodyConflictCheck()
                .flatMapMany(check -> check.apply(body));
    }

}