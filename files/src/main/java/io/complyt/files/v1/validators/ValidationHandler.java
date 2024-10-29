package io.complyt.files.v1.validators;

import io.complyt.files.utils.observability.ContextLogger;
import io.complyt.files.v1.exceptions.types.ObjectNotValidApiException;
import io.complyt.files.v1.validators.query_params.QueryParamsExtractor;
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

    QueryParamsExtractor<T> queryParamsExtractor;

    /**
     * @return
     */
    public final @NonNull Mono<T> handle(final ServerRequest serverRequest) {
        return validateRequest(serverRequest);
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
        return queryParamsExtractor.extract(serverRequest)
                .flatMap(this::validateObject)
                .switchIfEmpty(serverRequest.bodyToMono(validationClass)
                        .flatMap(this::validateObject));
    }

    private Mono<T> validateObject(T objectToValidate) {
        Errors errors = new BeanPropertyBindingResult(objectToValidate, validationClass.getName());
        validator.validate(objectToValidate, errors);

        if (errors.getAllErrors().isEmpty()) {
            return Mono.just(objectToValidate);
        } else {
            return ContextLogger.observeCtx("Failed to validate the format " + errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList(), log::info)
                    .then(onValidationErrors(errors));
        }
    }

    public final Mono<T> validate(final ServerRequest request) {
        return request.bodyToMono(validationClass).flatMap(body -> {
            Errors errors = new BeanPropertyBindingResult(body, validationClass.getName());
            validator.validate(body, errors);

            if (errors.getAllErrors().isEmpty()) {
                return Mono.just(body);
            } else {
                return onValidationErrors(errors);
            }
        });
    }

    private Mono<T> onValidationErrors(Errors errors) {
        return Mono.error(new ObjectNotValidApiException(errors));
    }
}