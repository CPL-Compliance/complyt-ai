package com.complyt.v1.handlers;

import com.complyt.business.pagination.PaginationConstants;
import com.complyt.domain.customer.CustomerLookupDetail;
import com.complyt.facades.TransactionFacade;
import com.complyt.security.permissions.transaction.TransactionCreatePermission;
import com.complyt.security.permissions.transaction.TransactionDeletePermission;
import com.complyt.security.permissions.transaction.TransactionReadPermission;
import com.complyt.services.CustomerDeterminationUtility;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.transaction.TransactionMapper;
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
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionHandler {

    @NonNull
    TransactionFacade transactionFacade;

    @NonNull
    CustomerDeterminationUtility customerDeterminationUtility;

    @NonNull
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler;

//    @NonNull
//    Patcher<TransactionDto> transactionPatcher;

    @TransactionReadPermission
    public Mono<ServerResponse> getByExternalIdAndSource(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");

        boolean detailed = Boolean.parseBoolean(serverRequest.queryParam("detailed").orElse("false"));

        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<TransactionDto> transactionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> transactionFacade.findByExternalIdAndSource(externalId, source,
                                detailed))
                        .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                        .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto,
                                log::info).thenReturn(transactionDto))
                        .switchIfEmpty(ContextLogger.observeCtx("Failed to get transaction by externalId " + externalId + " and source " + source, log::error)
                                .then(Mono.error(new ObjectNotFoundApiException()))));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono,
                TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        String page = serverRequest.queryParam("page")
                .orElse(String.valueOf(PaginationConstants.DEFAULT_PAGE_NUM));
        String size = serverRequest.queryParam("size")
                .orElse(String.valueOf(PaginationConstants.DEFAULT_PAGE_SIZE));
        String sortOrder = serverRequest.queryParam("sortOrder")
                .orElse(PaginationConstants.DEFAULT_SORT_ORDER);
        String sortBy = serverRequest.queryParam("sortBy")
                .orElse(PaginationConstants.DEFAULT_TRANSACTION_SORT_BY);

        boolean detailed = Boolean.parseBoolean(serverRequest.queryParam("detailed").orElse("false"));


        Map<String, String> filterMap = serverRequest.queryParams().toSingleValueMap();

        Flux<TransactionDto> transactionDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> transactionFacade.getAll(Integer.parseInt(page),
                                Integer.parseInt(size), filterMap, sortOrder, sortBy, detailed))
                        .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                        .flatMapSequential(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto, log::info).thenReturn(transactionDto)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoFlux,
                TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getAllBySource(ServerRequest serverRequest) {
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("-quest Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Flux<TransactionDto> transactionDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> transactionFacade.getAllBySource(source))
                        .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                        .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto,
                                log::info).thenReturn(transactionDto)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoFlux,
                TransactionDto.class);
    }

    @TransactionReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        String complytIdAsString = serverRequest.pathVariable("complytId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<TransactionDto> transactionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> transactionFacade.findByComplytId(UUID.fromString(complytIdAsString))
                        .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                        .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " + transactionDto,
                                log::info).thenReturn(transactionDto))
                        .switchIfEmpty(ContextLogger.observeCtx("Failed to get transaction by complytId " + complytIdAsString, log::error)
                                .then(Mono.error(new ObjectNotFoundApiException())))));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono,
                TransactionDto.class);
    }

    @TransactionCreatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());
        String resourceURI = TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId;

        return ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.handle(serverRequest)
                        .flatMap(transactionDto -> ContextLogger.observeCtx("--> Body: " + transactionDto, log::info)
                                .thenReturn(transactionDto))
                        .flatMap(transactionDto -> customerDeterminationUtility
                                .determineCustomerForTransaction( new CustomerLookupDetail(
                                        transactionDto.customerId(),
                                        transactionDto.customerExternalRef(),
                                        transactionDto.customerSource())
                                )
                                .map(customer -> TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto).setCustomer(customer).setCustomerId(customer.getComplytId())))
                        .flatMap(receivedTransaction ->
                                transactionFacade.findByExternalIdAndSource(externalId, source)
                                        .flatMap(originalTransaction -> transactionFacade.update(externalId, source, receivedTransaction, originalTransaction)
                                                .flatMap(savedTransaction -> ContextLogger.observeCtx("<-- Returned Body: " + savedTransaction, log::info)
                                                        .thenReturn(savedTransaction))
                                                .flatMap(savedTransaction -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                                        .body(Mono.just(TransactionMapper.INSTANCE.transactionToTransactionDto(savedTransaction)), TransactionDto.class))
                                                .switchIfEmpty(ServerResponse.noContent().build()
                                                        .flatMap(serverResponse -> ContextLogger.observeCtx("<-- No Content: Status code " + serverResponse.statusCode(), log::info).thenReturn(serverResponse))))
                                        .switchIfEmpty(Mono.defer(() -> transactionFacade.saveTransaction(receivedTransaction)
                                                        .flatMap(savedTransaction -> ContextLogger.observeCtx("<-- Returned Body: " + savedTransaction, log::info).thenReturn(savedTransaction))
                                                        .flatMap(savedTransaction -> ServerResponse.created(URI.create(resourceURI)).contentType(MediaType.APPLICATION_JSON).body(Mono.just(TransactionMapper.INSTANCE.transactionToTransactionDto(savedTransaction)), TransactionDto.class)))
                                                .switchIfEmpty(ServerResponse.noContent().build()
                                                        .flatMap(serverResponse -> ContextLogger.observeCtx("<-- No Content: Status code " + serverResponse.statusCode(), log::info).thenReturn(serverResponse))))));
    }

    @TransactionDeletePermission
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<TransactionDto> transactionDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(transactionDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> transactionFacade.markAsCancelled(externalId, source))
                        .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
                        .switchIfEmpty(ContextLogger.observeCtx("Failed to delete transaction by externalId " + externalId + " and source " + source, log::error)
                                .then(Mono.error(new ObjectNotFoundApiException()))));

        return transactionDtoMono.switchIfEmpty(transactionDtoMono)
                .flatMap(response -> ServerResponse.noContent().build()
                        .flatMap(serverResponse -> ContextLogger.observeCtx("<-- No Content: Status code " + serverResponse.statusCode(), log::info).thenReturn(serverResponse)));
    }

//    public Mono<ServerResponse> patch(ServerRequest serverRequest) {
//        String complytId = serverRequest.pathVariable("complytId");
//        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
//        serverRequest.path());
//
//        Mono<TransactionDto> transactionDtoMono = ContextLogger.observeCtx(logStr, log::info)
//                .then(transactionDtoValidationHandler.validateParam("complytId", complytId))
//                .then(Mono.defer(() -> transactionFacade.findByComplytId(UUID.fromString(complytId)))
//                        .flatMap(existingTransaction -> serverRequest.bodyToMono(Map.class)
//                                .map(map -> transactionPatcher.patch(TransactionMapper.INSTANCE
//                                .transactionToTransactionDto(existingTransaction), map))
//                                .flatMap(transactionDto -> transactionDtoValidationHandler.handle(transactionDto,
//                                serverRequest.pathVariables().entrySet()))
//                                .flatMap(transactionDto -> transactionFacade.update(transactionDto.externalId(),
//                                transactionDto.source(), TransactionMapper.INSTANCE.transactionDtoToTransaction
//                                (transactionDto), existingTransaction))
//                                .map(TransactionMapper.INSTANCE::transactionToTransactionDto)
//                                .flatMap(transactionDto -> ContextLogger.observeCtx("<-- Returned Body: " +
//                                transactionDto, log::info).thenReturn(transactionDto)))
//                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));
//
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(transactionDtoMono, TransactionDto
//        .class);
//    }

}