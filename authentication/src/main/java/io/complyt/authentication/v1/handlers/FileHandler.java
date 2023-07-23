package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.services.ApiKeyService;
import io.complyt.authentication.v1.mappers.FileMapper;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.security.permissions.LinkReadPermission;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
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
public class FileHandler {
    @NonNull
    ApiKeyService apiKeyService;

    @LinkReadPermission
    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<ApiKeyDto> value = ContextLogger.observeCtx(logStr, log::info).then(apiKeyService.find())
                .map(FileMapper.INSTANCE::fileToFileDto)
                .flatMap(fileDto -> ContextLogger.observeCtx("<-- Returned Body: " + fileDto.toString(), log::info).thenReturn(fileDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().body(value, ApiKeyDto.class);
    }
}