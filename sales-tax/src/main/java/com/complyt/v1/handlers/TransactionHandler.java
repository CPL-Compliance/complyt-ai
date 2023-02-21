package com.complyt.v1.handlers;

import com.complyt.facades.TransactionFacade;
import com.complyt.security.permissions.transaction.TransactionCreatePermission;
import com.complyt.security.permissions.transaction.TransactionDeletePermission;
import com.complyt.security.permissions.transaction.TransactionReadPermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.models.TransactionDto;
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
                .then(transactionFacade.findByExternalIdAndSource(externalId, source))
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<TransactionDto> transactionDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(transactionFacade.getAll())
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoFlux, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getAllBySource(ServerRequest serverRequest) {
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<TransactionDto> transactionDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(transactionFacade.getAllBySource(source))
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoFlux, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        String complytIdAsString = serverRequest.pathVariable("complytId");
        UUID complytId = UUID.fromString(complytIdAsString);
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<TransactionDto> transactionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(transactionFacade.findByComplytId(complytId))
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono, TransactionDto.class);
    }

    @TransactionCreatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        return ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.validate(serverRequest))
                .flatMap(transactionDto -> ContextLogger.observeCtx("--> Body: " + transactionDto, log::info).thenReturn(transactionDto))
                .map(TransactionMapper.INSTANCE::transactionDtoToTransaction)
                .flatMap(receivedTransaction ->
                        transactionFacade.findByExternalIdAndSource(externalId, source)
                                .flatMap(originalTransaction -> transactionFacade.updateIfModified(externalId, source, receivedTransaction, originalTransaction)
                                        .flatMap(savedTransaction -> ContextLogger.observeCtx("<-- Returned Body: " + savedTransaction, log::info).thenReturn(savedTransaction))
                                        .flatMap(savedTransaction -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(TransactionMapper.INSTANCE.transactionToTransactionDto(savedTransaction)), TransactionDto.class)))
                                .switchIfEmpty(transactionFacade.saveTransaction(receivedTransaction)
                                        .flatMap(savedTransaction -> ContextLogger.observeCtx("<-- Returned Body: " + savedTransaction, log::info).thenReturn(savedTransaction))
                                        .flatMap(savedTransaction -> ServerResponse.created(serverRequest.uri()).contentType(MediaType.APPLICATION_JSON).body(Mono.just(TransactionMapper.INSTANCE.transactionToTransactionDto(savedTransaction)), TransactionDto.class))));
    }

    @TransactionDeletePermission
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        return ContextLogger.observeCtx(logStr, log::info)
                .then(transactionFacade.markAsCancelled(externalId, source))
                .flatMap(transaction -> ServerResponse.noContent().build())
                .flatMap(serverResponse -> ContextLogger.observeCtx("<-- No Content: Status code " + serverResponse.statusCode(), log::info).thenReturn(serverResponse))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
    }
}