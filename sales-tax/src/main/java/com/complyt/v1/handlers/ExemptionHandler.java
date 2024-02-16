package com.complyt.v1.handlers;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.facades.ExemptionFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.permissions.exemption.ExemptionCreatePermission;
import com.complyt.security.permissions.exemption.ExemptionDeletePermission;
import com.complyt.security.permissions.exemption.ExemptionReadPermission;
import com.complyt.security.permissions.exemption.ExemptionUpdatePermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.mappers.ExemptionWrapperMapper;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import com.complyt.v1.routers.ExemptionRouter;
import com.complyt.v1.validators.PatchingHandler;
import com.complyt.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
public class ExemptionHandler {

    @NonNull
    private ExemptionFacade exemptionFacade;

    @NonNull
    ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler;

    @NonNull
    ValidationHandler<ExemptionWrapperDto, SpringValidatorAdapter> exemptionWrapperDtoValidationHandler;

    @NonNull
    PatchingHandler<ExemptionDto, SpringValidatorAdapter> exemptionPatchingHandler;

    @ExemptionReadPermission
    public Mono<ServerResponse> findByComplytId(ServerRequest serverRequest) {
        String complytId = serverRequest.pathVariable("complytId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<ExemptionDto> exemptionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(exemptionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> exemptionFacade.findByComplytId(UUID.fromString(complytId)))
                        .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                        .flatMap(exemptionDto -> ContextLogger.observeCtx("<-- Returned Body: " + exemptionDto, log::info).thenReturn(exemptionDto))
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(exemptionDtoMono, ExemptionDto.class);
    }

    @ExemptionUpdatePermission
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        String complytId = serverRequest.pathVariable("complytId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<ExemptionDto> exemptionDtoMono = ContextLogger.observeCtx(logStr, log::info).then(exemptionDtoValidationHandler.handle(serverRequest))
                .flatMap(exemptionDto -> {
                    Exemption receivedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);
                    return exemptionFacade.update(receivedExemption, UUID.fromString(complytId));
                })
                .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                .flatMap(exemptionDto -> ContextLogger.observeCtx("<-- Returned Body: " + exemptionDto, log::info).thenReturn(exemptionDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(exemptionDtoMono, ExemptionDto.class);
    }

    @ExemptionCreatePermission
    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<ExemptionDto> exemptionDtoFlux = ContextLogger.observeCtx(logStr, log::info).then(exemptionWrapperDtoValidationHandler.handle(serverRequest))
                .flatMapMany(exemptionWrapperDto -> {
                    ExemptionWrapper receivedExemptionWrapper = ExemptionWrapperMapper.INSTANCE.exemptionWrapperDtoToExemptionWrapper(exemptionWrapperDto);
                    return exemptionFacade.save(receivedExemptionWrapper);
                })
                .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                .flatMap(exemptionDto -> ContextLogger.observeCtx("<-- Returned Body: " + exemptionDto, log::info).thenReturn(exemptionDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.created(URI.create(ExemptionRouter.BASE_URL)).contentType(MediaType.APPLICATION_JSON).body(exemptionDtoFlux, ExemptionDto.class);
    }

    @ExemptionReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        String page = serverRequest.queryParam("page")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_NUM));
        String size = serverRequest.queryParam("size")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_SIZE));

        Flux<ExemptionDto> exemptionDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(exemptionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> exemptionFacade.findAll(Integer.parseInt(page), Integer.parseInt(size)))
                        .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                        .flatMapSequential(exemptionDto -> ContextLogger.observeCtx("<-- Returned Body: " + exemptionDto, log::info).thenReturn(exemptionDto)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(exemptionDtoFlux, ExemptionDto.class);
    }

    @ExemptionDeletePermission
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String complytId = serverRequest.pathVariable("complytId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<ExemptionDto> exemptionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(exemptionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> exemptionFacade.markAsCancelled(UUID.fromString(complytId)))
                        .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));

        return exemptionDtoMono.switchIfEmpty(exemptionDtoMono)
                .flatMap(response -> ServerResponse.noContent().build()
                        .flatMap(serverResponse -> ContextLogger.observeCtx("<-- No Content: Status code " + serverResponse.statusCode(), log::info).thenReturn(serverResponse)));

    }

    public Mono<ServerResponse> patch(ServerRequest serverRequest) {
        String complytId = serverRequest.pathVariable("complytId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<ExemptionDto> exemptionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(exemptionFacade.findByComplytId(UUID.fromString(complytId)))
                .flatMap(existingExemption -> {
                    ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(existingExemption);

                    return exemptionPatchingHandler.patch(serverRequest, exemptionDto)
                            .map(ExemptionMapper.INSTANCE::exemptionDtoToExemption)
                            .flatMap(exemption -> exemptionFacade.update(exemption, UUID.fromString(complytId)));
                })
                .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                .flatMap(exemptionDto -> ContextLogger.observeCtx("<-- Returned Body: " + exemptionDto, log::info).thenReturn(exemptionDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(exemptionDtoMono, ExemptionDto.class);
    }
}