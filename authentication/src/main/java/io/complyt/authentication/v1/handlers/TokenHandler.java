package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.services.TokenService;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.authentication.v1.mappers.TokenMapper;
import io.complyt.authentication.v1.models.TokenDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
public class TokenHandler {
    @NonNull
    TokenService tokenService;

    public Mono<ServerResponse> post(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<TokenDto> value = ContextLogger.observeCtx(logStr, log::info).then(tokenService.find())
                .map(TokenMapper.INSTANCE::tokentoTokenDto)
                .flatMap(apiKeyDto -> ContextLogger.observeCtx("<-- Returned Body: " + apiKeyDto.toString(), log::info).thenReturn(apiKeyDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().body(value, TokenDto.class);
    }
}