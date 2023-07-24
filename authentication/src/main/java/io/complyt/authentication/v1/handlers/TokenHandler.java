package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.facades.TokenFacade;
import io.complyt.authentication.services.TokenService;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.authentication.v1.mappers.TokenMapper;
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
public class TokenHandler {
    @NonNull
    TokenFacade tokenFacade;

    @NonNull
    ValidationHandler<TokenDto, SpringValidatorAdapter> tokenDtoValidationHandler;

    public Mono<ServerResponse> post(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<TokenDto> value = ContextLogger.observeCtx(logStr, log::info).then(tokenDtoValidationHandler.validate(serverRequest))
                .flatMap(tokenDto -> ContextLogger.observeCtx(tokenDto.toString(), log::info).thenReturn(tokenDto))
                .map(TokenMapper.INSTANCE::tokenDtoToToken)
                .flatMap(tokenFacade::get)
                .map(TokenMapper.INSTANCE::tokentoTokenDto)
                .flatMap(apiKeyDto -> ContextLogger.observeCtx("<-- Returned Body: " + apiKeyDto.toString(), log::info).thenReturn(apiKeyDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().body(value, TokenDto.class);
    }
}