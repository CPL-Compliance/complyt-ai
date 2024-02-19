package com.complyt.v1.handlers;

import com.complyt.facades.TransactionFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.permissions.transaction.TransactionCreatePermission;
import com.complyt.security.permissions.transaction.TransactionDeletePermission;
import com.complyt.security.permissions.transaction.TransactionReadPermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.routers.TransactionRouter;
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
public class TransactionHandler {

    @NonNull
    TransactionFacade transactionFacade;

    @NonNull
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler;

    @TransactionReadPermission
    public Mono<ServerResponse> getByExternalIdAndSource(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");

        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<TransactionDto> transactionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> transactionFacade.findByExternalIdAndSource(externalId, source))
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        String page = serverRequest.queryParam("page")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_NUM));
        String size = serverRequest.queryParam("size")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_SIZE));

        Flux<TransactionDto> transactionDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> transactionFacade.getAll(Integer.parseInt(page), Integer.parseInt(size)))
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                .flatMapSequential(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoFlux, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getAllBySource(ServerRequest serverRequest) {
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<TransactionDto> transactionDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> transactionFacade.getAllBySource(source))
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoFlux, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        String complytIdAsString = serverRequest.pathVariable("complytId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<TransactionDto> transactionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> transactionFacade.findByComplytId(UUID.fromString(complytIdAsString))
                        .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                        .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto))
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()))));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono, TransactionDto.class);
    }

    @TransactionCreatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        String resourceURI = TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId;

        return ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.handle(serverRequest)
                        .flatMap(transactionDto -> ContextLogger.observeCtx("--> Body: " + transactionDto, log::info).thenReturn(transactionDto))
                        .map(TransactionMapper.INSTANCE::transactionDtoToTransaction)
                        .flatMap(receivedTransaction ->
                                transactionFacade.findByExternalIdAndSource(externalId, source)
                                        .flatMap(originalTransaction -> transactionFacade.updateIfModified(externalId, source, receivedTransaction, originalTransaction)
                                                .flatMap(savedTransaction -> ContextLogger.observeCtx("<-- Returned Body: " + savedTransaction, log::info).thenReturn(savedTransaction))
                                                .flatMap(savedTransaction -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(TransactionMapper.INSTANCE.transactionToTransactionDto(savedTransaction)), TransactionDto.class)))
                                        .switchIfEmpty(Mono.defer(() -> transactionFacade.saveTransaction(receivedTransaction)
                                                .flatMap(savedTransaction -> ContextLogger.observeCtx("<-- Returned Body: " + savedTransaction, log::info).thenReturn(savedTransaction))
                                                .flatMap(savedTransaction -> ServerResponse.created(URI.create(resourceURI)).contentType(MediaType.APPLICATION_JSON).body(Mono.just(TransactionMapper.INSTANCE.transactionToTransactionDto(savedTransaction)), TransactionDto.class))))));
    }

    @TransactionDeletePermission
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<TransactionDto> transactionDtoMono =  ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> transactionFacade.markAsCancelled(externalId, source))
                        .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));


        return transactionDtoMono.switchIfEmpty(transactionDtoMono)
                .flatMap(response -> ServerResponse.noContent().build()
                .flatMap(serverResponse -> ContextLogger.observeCtx("<-- No Content: Status code " + serverResponse.statusCode(), log::info).thenReturn(serverResponse)));
    }

}