package io.complyt.authentication.v1.validators;

import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotValidApiException;
import io.complyt.authentication.v1.exceptions.types.QueryParamErrorException;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidationHandler<T, U extends Validator> {

    @NonNull
    Class<T> validationClass;

    @NonNull
    U validator;

    @NonNull
    QueryParamsExtractor<T> queryParamsExtractor;

    @NonNull
    ParameterChecksProvider queryParamChecksProvider;

    @NonNull
    ShouldCallValidate shouldCallValidate;

    /**
     * @param serverRequest
     * @return
     */
    public Mono<T> handle(final ServerRequest serverRequest) {
        return validateQueryParam(serverRequest)
                .then(Mono.defer(() -> shouldCallValidate.apply(serverRequest) ? validateRequest(serverRequest) : Mono.empty()));
    }

    /**
     * This method employs query parameter validation by converting them into an object and subsequently
     * validating the object. If no query parameters are present in the request, it then examines the request
     * body for validation purposes.
     *
     * @param serverRequest - The request to validate
     * @return - Mono of valid object or Mono error
     */
    private @NonNull Mono<T> validateRequest(final ServerRequest serverRequest) {
        return queryParamsExtractor.extract(serverRequest).flatMap(this::validateObject)
                .switchIfEmpty(serverRequest.bodyToMono(validationClass).flatMap(this::validateObject));
    }

    @NonNull
    private Mono<T> validateObject(T objectToValidate) {
        Errors errors = new BeanPropertyBindingResult(objectToValidate, validationClass.getName());
        validator.validate(objectToValidate, errors);

        if (errors.getAllErrors().isEmpty()) {
            return Mono.just(objectToValidate);
        } else {
            return ContextLogger.observeCtx("Failed to validate the format " + errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList(), log::info)
                    .then(onValidationErrors());
        }
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

    private @NonNull Mono<T> onValidationErrors() {
        return Mono.error(new ObjectNotValidApiException());
    }
}