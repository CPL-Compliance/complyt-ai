package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.facades.ApiKeyFacade;
import io.complyt.authentication.security.permissions.api_key.ApiKeyCreatePermission;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.authentication.v1.mappers.ApiKeyMapper;
import io.complyt.authentication.v1.mappers.CredentialsMapper;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.v1.routers.ApiKeyRouter;
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

import java.net.URI;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyHandler {
    @NonNull
    ApiKeyFacade apiKeyFacade;

    @NonNull
    ValidationHandler<CredentialsDto, SpringValidatorAdapter> credentialsDtoValidationHandler;

    @NonNull
    ValidationHandler<ApiKeyDto, SpringValidatorAdapter> apiKeyDtoValidationHandler;


    @ApiKeyCreatePermission
    public Mono<ServerResponse> post(@NonNull ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<ApiKeyDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(credentialsDtoValidationHandler.handle(serverRequest))
                .map(CredentialsMapper.INSTANCE::credentialsDtoTocredentials)
                .flatMap(apiKeyFacade::saveCredentials)
                .map(ApiKeyMapper.INSTANCE::apiKeyToApiKeyDto)
                .flatMap(apiKeyDto -> ContextLogger.observeCtx("<-- Returned Body: " + apiKeyDto.toString(), log::info).thenReturn(apiKeyDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.created(URI.create(ApiKeyRouter.BASE_URL)).body(value, TokenDto.class);
    }

    public Mono<ServerResponse> delete(@NonNull ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        return ContextLogger.observeCtx(logStr, log::info)
                .then(apiKeyDtoValidationHandler.handle(serverRequest))
                .map(ApiKeyMapper.INSTANCE::apiKeyDtoToApiKey)
                .flatMap(apiKeyFacade::markAsCancelled)
                .then(ServerResponse.noContent().build())
                .flatMap(serverResponse -> ContextLogger.observeCtx("<-- No Content: Status code "
                        + serverResponse.statusCode(), log::info).thenReturn(serverResponse));
    }
}