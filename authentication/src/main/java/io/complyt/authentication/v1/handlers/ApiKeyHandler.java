package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.facades.ApiKeyFacade;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.authentication.v1.mappers.CredentialsMapper;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyHandler {
    @NonNull
    ApiKeyFacade apiKeyFacade;


    @NonNull
    ValidationHandler<CredentialsDto, SpringValidatorAdapter> credentialsDtoValidationHandler;

    public Mono<ServerResponse> post(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<ApiKeyDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(credentialsDtoValidationHandler.validate(serverRequest))
                .map(CredentialsMapper.INSTANCE::credentialsDtoTocredentials)
                .flatMap(apiKeyFacade::saveCredentials)
                .map(apiKeyStr -> new ApiKeyDto(apiKeyStr))
                .flatMap(apiKeyDto -> ContextLogger.observeCtx("<-- Returned Body: " + apiKeyDto.toString(), log::info).thenReturn(apiKeyDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.created(serverRequest.uri()).body(value, TokenDto.class);
    }
}