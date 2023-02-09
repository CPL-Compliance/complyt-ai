package com.complyt.v1.validators;

import com.complyt.domain.State;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.models.fields.ComplytIdFieldModel;
import com.complyt.v1.models.fields.ExternalIdAndSourceFieldsModel;
import com.complyt.v1.models.fields.NameFieldModel;
import com.complyt.v1.models.fields.StateFieldModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidationHandler<T, U extends Validator> {

    @NonNull
    Class<T> validationClass;

    @NonNull
    U validator;

    private Mono<T> onValidationErrors(Errors errors) {
        return Mono.error(new ObjectNotValidApiException(errors));
    }

    public final Mono<T> validate(final ServerRequest request) {
        return request.bodyToMono(validationClass)
                .flatMap(body -> {
                    Errors errors = new BeanPropertyBindingResult(body, validationClass.getName());
                    validator.validate(body, errors);

                    if (errors.getAllErrors().isEmpty()) {
                        return Mono.just(body);
                    } else {
                        return onValidationErrors(errors);
                    }
                });
    }

    public <M extends ExternalIdAndSourceFieldsModel> Mono<M> checkExternalIdAndSourceConflict(M resource, String externalId, String source) {
        return externalId.equals(resource.externalId()) && source.equals(resource.source()) ?
                Mono.just(resource) : Mono.error(new ConflictedDataApiException());
    }

    public <M extends ComplytIdFieldModel> Mono<M> checkComplytIdConflict(M resource, UUID complytId) {
        return complytId.equals(resource.complytId()) ?
                Mono.just(resource) : Mono.error(new ConflictedDataApiException());
    }

    public <M extends StateFieldModel> Mono<M> checkStateConflict(M resource, State state) {
        return state.equals(resource.state()) ?
                Mono.just(resource) : Mono.error(new ConflictedDataApiException());
    }

    public <M extends NameFieldModel> Mono<M> checkNameConflict(M resource, String name) {
        return name.equals(resource.name()) ?
                Mono.just(resource) : Mono.error(new ConflictedDataApiException());
    }
}