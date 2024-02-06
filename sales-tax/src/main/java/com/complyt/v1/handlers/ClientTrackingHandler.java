package com.complyt.v1.handlers;

import com.complyt.facades.ClientTrackingFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.permissions.client_tracking.ClientTrackingReadPermission;
import com.complyt.security.permissions.client_tracking.ClientTrackingUpdatePermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ClientTrackingMapper;
import com.complyt.v1.models.ClientTrackingDto;
import com.complyt.v1.routers.ClientTrackingRouter;
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

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientTrackingHandler {

    @NonNull
    ClientTrackingFacade clientTrackingFacade;

    @NonNull
    ValidationHandler<ClientTrackingDto, SpringValidatorAdapter> clientTrackingDtoValidationHandler;

    @ClientTrackingReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        String page = serverRequest.queryParam("page")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_NUM));
        String size = serverRequest.queryParam("size")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_SIZE));

        Flux<ClientTrackingDto> clientTrackingDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(clientTrackingDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> clientTrackingFacade.getAll(Integer.parseInt(page), Integer.parseInt(size))
                .map(ClientTrackingMapper.INSTANCE::clientTrackingToClientTrackingDto)
                .flatMapSequential(clientTrackingDto -> ContextLogger.observeCtx("<-- Returned Body: " + clientTrackingDto, log::info).thenReturn(clientTrackingDto))));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(clientTrackingDtoFlux, ClientTrackingDto.class);
    }

    @ClientTrackingReadPermission
    public Mono<ServerResponse> getByName(ServerRequest serverRequest) {
        String name = serverRequest.pathVariable("name");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<ClientTrackingDto> clientTrackingDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(clientTrackingFacade.getByName(name))
                .map(ClientTrackingMapper.INSTANCE::clientTrackingToClientTrackingDto)
                .flatMap(clientTrackingDto -> ContextLogger.observeCtx("<-- Returned Body: " + clientTrackingDto, log::info).thenReturn(clientTrackingDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(clientTrackingDtoFlux, ClientTrackingDto.class);
    }

    @ClientTrackingReadPermission
    public Mono<ServerResponse> getByTenantId(ServerRequest serverRequest) {
        String tenantId = serverRequest.pathVariable("tenantId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<ClientTrackingDto> clientTrackingDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(clientTrackingFacade.getByTenantId(tenantId))
                .map(ClientTrackingMapper.INSTANCE::clientTrackingToClientTrackingDto)
                .flatMap(clientTrackingDto -> ContextLogger.observeCtx("<-- Returned Body: " + clientTrackingDto, log::info).thenReturn(clientTrackingDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(clientTrackingDtoFlux, ClientTrackingDto.class);
    }

    @ClientTrackingUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String tenantId = serverRequest.pathVariable("tenantId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        String resourceURI = ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId;


        return ContextLogger.observeCtx(logStr, log::info)
                .then(clientTrackingDtoValidationHandler.handle(serverRequest))
                .map(ClientTrackingMapper.INSTANCE::clientTrackingDtoToClientTracking)
                .flatMap(receivedClientTracking ->
                        clientTrackingFacade.getByTenantId(tenantId)
                        .flatMap(originalClientTracking -> clientTrackingFacade.updateIfModified(receivedClientTracking, originalClientTracking)
                        .flatMap(savedClientTracking -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(ClientTrackingMapper.INSTANCE.clientTrackingToClientTrackingDto(savedClientTracking))
                                        .flatMap(clientTrackingDto -> ContextLogger.observeCtx("<-- Returned Body: " + clientTrackingDto, log::info)
                                                .thenReturn(clientTrackingDto)), ClientTrackingDto.class)))

                        .switchIfEmpty(clientTrackingFacade.saveClientTracking(receivedClientTracking, tenantId).flatMap(savedClientTracking ->
                                ServerResponse.created(URI.create(resourceURI)).contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(ClientTrackingMapper.INSTANCE.clientTrackingToClientTrackingDto(savedClientTracking))
                                        .flatMap(clientTrackingDto -> ContextLogger.observeCtx("<-- Returned Body: " + clientTrackingDto, log::info)
                                                .thenReturn(clientTrackingDto)), ClientTrackingDto.class))));
    }
}
