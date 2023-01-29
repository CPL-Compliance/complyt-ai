package io.complyt.files.v1.handlers;

import io.complyt.files.security.permissions.LinkReadPermission;
import io.complyt.files.services.FileService;
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
    FileService fileService;

    @LinkReadPermission
    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        Mono<FileDto> value = fileService.find()
                .map(FileMapper.INSTANCE::fileToFileDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().body(value, FileDto.class);
    }
}