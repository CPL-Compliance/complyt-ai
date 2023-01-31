package com.complyt.v1.handlers;

import com.complyt.facades.TransactionFacade;
import com.complyt.security.permissions.transaction.TransactionCreatePermission;
import com.complyt.security.permissions.transaction.TransactionReadPermission;
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

        log.debug("Get Transaction by external id and source - id and source received as path variables : " + externalId + ", " + source);
        Mono<TransactionDto> transactionDtoMono = transactionFacade.findByExternalIdAndSource(externalId, source)
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        Flux<TransactionDto> transactionDtoFlux = transactionFacade.getAll()
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoFlux, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getAllBySource(ServerRequest serverRequest) {
        String source = serverRequest.pathVariable("source");

        Flux<TransactionDto> transactionDtoFlux = transactionFacade.getAllBySource(source)
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoFlux, TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        String complytIdAsString = serverRequest.pathVariable("complytId");
        UUID complytId = UUID.fromString(complytIdAsString);

        Mono<TransactionDto> transactionDtoMono = transactionFacade.findByComplytId(complytId)
                .map(TransactionMapper.INSTANCE::transactionToTransactionDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono, TransactionDto.class);
    }

    @TransactionCreatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");

        return transactionDtoValidationHandler.validate(serverRequest)
                .map(TransactionMapper.INSTANCE::transactionDtoToTransaction)
                .flatMap(receivedTransaction ->
                        transactionFacade.findByExternalIdAndSource(externalId, source)
                                .flatMap(originalTransaction -> transactionFacade.updateIfModified(externalId, source, receivedTransaction, originalTransaction)
                                        .flatMap(savedTransaction -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(TransactionMapper.INSTANCE.transactionToTransactionDto(savedTransaction)), TransactionDto.class)))
                                .switchIfEmpty(transactionFacade.saveTransaction(receivedTransaction).flatMap(savedTransaction -> ServerResponse.created(serverRequest.uri()).contentType(MediaType.APPLICATION_JSON).body(Mono.just(TransactionMapper.INSTANCE.transactionToTransactionDto(savedTransaction)), TransactionDto.class))));
    }

}
