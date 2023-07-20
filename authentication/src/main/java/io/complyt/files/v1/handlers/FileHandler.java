package io.complyt.files.v1.handlers;

import io.complyt.files.security.permissions.LinkReadPermission;
import io.complyt.files.services.ApiKeyService;
import io.complyt.files.utils.observability.ContextLogger;
import io.complyt.files.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.files.v1.mappers.FileMapper;
import io.complyt.files.v1.models.FileDto;
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

        Mono<FileDto> value = ContextLogger.observeCtx(logStr, log::info).then(apiKeyService.find())
                .map(FileMapper.INSTANCE::fileToFileDto)
                .flatMap(fileDto -> ContextLogger.observeCtx("<-- Returned Body: " + fileDto.toString(), log::info).thenReturn(fileDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().body(value, FileDto.class);
    }
}