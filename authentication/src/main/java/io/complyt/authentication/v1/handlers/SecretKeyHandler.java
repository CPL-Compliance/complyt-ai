package io.complyt.authentication.v1.handlers;


import io.complyt.authentication.security.SecretKeyUtils;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.authentication.v1.mappers.CredentialsMapper;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.SecretKeyDto;
import io.complyt.authentication.v1.models.TokenDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecretKeyHandler {
    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<ApiKeyDto> value = ContextLogger.observeCtx(logStr, log::info)
                .flatMap(SecretKeyUtils.generateKey(256))
                .flatMap(SecretKeyUtils::convertSecretKeyToString)
                .map(secretKey -> new SecretKeyDto(secretKey))
                .flatMap(apiKeyDto -> ContextLogger.observeCtx("<-- Returned Body: " + apiKeyDto.toString(), log::info).thenReturn(apiKeyDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.created(serverRequest.uri()).body(value, TokenDto.class);
    }
}
