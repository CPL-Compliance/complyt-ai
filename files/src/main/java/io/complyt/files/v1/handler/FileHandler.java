package io.complyt.files.v1.handler;

import io.complyt.files.annotations.Generated;
import io.complyt.files.security.permissions.LinkReadPermission;
import io.complyt.files.services.FileService;
import io.complyt.files.v1.exception.ObjectNotFoundException;
import io.complyt.files.v1.mappers.FileMapper;
import io.complyt.files.v1.model.FileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Link", description = "This is the Links controller")
public class FileHandler {
    @NonNull
    private FileService fileService;

    @Operation(summary = "Gets link to the files")
    @ResponseStatus(HttpStatus.OK)
    @LinkReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        Mono<FileDto> value = fileService.find()
                .map(FileMapper.INSTANCE::fileToFileDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Link to the files not found")));

        return ServerResponse.ok().body(value, FileDto.class);
    }
}