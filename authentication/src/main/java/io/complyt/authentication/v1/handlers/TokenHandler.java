package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.facades.TokenFacade;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.authentication.v1.mappers.TokenMapper;
import io.complyt.authentication.v1.models.ApiKey;
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

import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenHandler {
    @NonNull
    TokenFacade tokenFacade;

    public Mono<ServerResponse> post(ServerRequest serverRequest) {
        Optional<String> apiKeyParam = serverRequest.queryParam("api_key");
        String apiKeyStr = apiKeyParam.get();
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s, Query Params -> %s", serverRequest.method(),
                serverRequest.path(),
                serverRequest.queryParams());

        Mono<TokenDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(Mono.just(new ApiKey(apiKeyStr)))
                .flatMap(tokenFacade::getToken)
                .map(TokenMapper.INSTANCE::tokentoTokenDto)
                .flatMap(tokenDto -> ContextLogger.observeCtx("<-- Returned Body: " + tokenDto.toString(), log::info).thenReturn(tokenDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().body(value, TokenDto.class);
    }
}