package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.MissingBodyApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
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

    public Mono<T> validate(final ServerRequest serverRequest) {
        return validateRequestBody(serverRequest)
                .flatMap(body -> Flux.fromIterable(serverRequest.pathVariables().keySet())
                        .flatMap(variable -> dataConflictChecksProvider.getPathVariableCheck(variable)
                                .flatMap(check -> check.apply(body, serverRequest)))
                        .concatWith(Flux.fromIterable(serverRequest.queryParams().keySet())
                                .flatMap(param -> dataConflictChecksProvider.getPathVariableCheck(param)
                                        .flatMap(check -> check.apply(body, serverRequest))))
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

    private Mono<T> checkErrorList(T object, List<String> errorList) {
        return errorList.isEmpty() ? Mono.just(object) :
                Mono.error(new ConflictedDataApiException(errorList));
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

    private Flux<String> checkBodyConflicts(T body) {
        return dataConflictChecksProvider.getBodyConflictCheck()
                .flatMapMany(check -> check.apply(body));
    }

}