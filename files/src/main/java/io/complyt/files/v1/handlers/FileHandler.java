package io.complyt.files.v1.handlers;

import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.facade.ComplytFileFacade;
import io.complyt.files.security.permissions.LinkReadPermission;
import io.complyt.files.services.FileService;
import io.complyt.files.utils.observability.ContextLogger;
import io.complyt.files.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.files.v1.mappers.ComplytFileMapper;
import io.complyt.files.v1.mappers.FileMapper;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import io.complyt.files.v1.models.FileDto;
import io.complyt.files.v1.routers.FileRouter;
import io.complyt.files.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileHandler {
    @NonNull
    FileService fileService;

    @NonNull
    ComplytFileFacade complytFileFacade;

    @NonNull
    ValidationHandler<ComplytFileDto, SpringValidatorAdapter> complytFileDtoValidationHandler;

    @LinkReadPermission
    public Mono<ServerResponse> get(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<FileDto> value = ContextLogger.observeCtx(logStr, log::info).then(fileService.find())
                .map(FileMapper.INSTANCE::fileToFileDto)
                .flatMap(fileDto -> ContextLogger.observeCtx("<-- Returned Body: " + fileDto.toString(), log::info).thenReturn(fileDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().body(value, FileDto.class);
    }

    public Mono<ServerResponse> getListOfFileInTenant(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        Boolean return_signed_link = serverRequest.queryParam("signed_link").map(Boolean::valueOf).orElse(false);
        String files_status_query = serverRequest.queryParam("status").map(String::valueOf).orElse("active");
        Flux<ComplytFileMetadata> values = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(complytFileFacade.findAllFilesInTenant(return_signed_link, files_status_query))
                .flatMapSequential(complytFileMetadata -> ContextLogger.observeCtx("<-- Returned Body: " + complytFileMetadata.toString(), log::info).thenReturn(complytFileMetadata))
                .switchIfEmpty(Mono.empty());

        return ServerResponse.ok().body(values, ComplytFileMetadata.class);
    }

    public Mono<ServerResponse> saveFile(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());


        Mono<ComplytFileMetadataDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(complytFileDtoValidationHandler.handle(serverRequest))
                .map(ComplytFileMapper.INSTANCE::complytFileDtoToComplytFile)
                .flatMap(complytFileFacade::saveFile)
                .map(ComplytFileMapper.INSTANCE::complytFileMetadataToComplytFileMetadataDto)
                .flatMap(complytFileMetadataDto -> ContextLogger.observeCtx("<-- Returned Body: " + complytFileMetadataDto.toString(), log::info).thenReturn(complytFileMetadataDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
        return ServerResponse.created(URI.create(FileRouter.COMPLYT_FILE_BASE_URL)).body(value, ComplytFileMetadataDto.class);
    }

    public Mono<ServerResponse> getFileWithSignedLink(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        Mono<ComplytFileMetadataDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(complytFileFacade.getSignedLinkForFile(UUID.fromString(serverRequest.pathVariable("complytId"))))
                .map(ComplytFileMapper.INSTANCE::complytFileMetadataToComplytFileMetadataDto)
                .flatMap(complytFileMetadataDto -> ContextLogger.observeCtx("<-- Returned Body: " + complytFileMetadataDto.toString(), log::info).thenReturn(complytFileMetadataDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
        return ServerResponse.ok().body(value, ComplytFileMetadata.class);
    }

    public Mono<ServerResponse> markAsDeleted(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        Mono<ComplytFileMetadataDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(complytFileFacade.markAsDeleted(UUID.fromString(serverRequest.pathVariable("complytId"))))
                .map(ComplytFileMapper.INSTANCE::complytFileMetadataToComplytFileMetadataDto)
                .flatMap(complytFileMetadataDto -> ContextLogger.observeCtx("<-- Returned Body: " + complytFileMetadataDto.toString(), log::info).thenReturn(complytFileMetadataDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
        return ServerResponse.ok().body(value, ComplytFileMetadata.class);
    }
}