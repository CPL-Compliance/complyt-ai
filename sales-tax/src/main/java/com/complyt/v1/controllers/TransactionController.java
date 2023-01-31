//package com.complyt.v1.controllers;
//
//import com.complyt.domain.Transaction;
//import com.complyt.facades.TransactionFacade;
//import com.complyt.security.permissions.transaction.TransactionDeletePermission;
//import com.complyt.security.permissions.transaction.TransactionReadPermission;
//import com.complyt.security.permissions.transaction.TransactionUpdatePermission;
//import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
//import com.complyt.v1.mappers.TransactionMapper;
//import com.complyt.v1.models.TransactionDto;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.AllArgsConstructor;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.UUID;
//
//@AllArgsConstructor
//@Slf4j
//@Tag(name = "Transaction", description = "This is the Transaction controller")
//@RestController
//@RequestMapping(TransactionController.BASE_URL)
//@SecurityRequirement(name = "bearerAuth")
//public class TransactionController {
//    public static final String BASE_URL = "/v1/transactions";
//
//    @NonNull
//    private TransactionFacade transactionFacade;
//
////    @Operation(summary = "Gets transaction by externalId and source")
////    @TransactionReadPermission
////    @GetMapping("source/{source}/externalId/{externalId}")
////    @ResponseStatus(HttpStatus.OK)
////    public Mono<ResponseEntity<TransactionDto>> getByExternalIdAndSource(@PathVariable("externalId") @NonNull String externalId, @PathVariable("source") @NonNull String source) {
////        log.debug("Get customer by external id and source - id and source received as path variables : " + externalId + ", " + source);
////        return transactionFacade.findByExternalIdAndSource(externalId, source)
////                .map(transactionItem -> new ResponseEntity<>(TransactionMapper.INSTANCE.transactionToTransactionDto(transactionItem), HttpStatus.OK))
////                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
////    }
//
//    @Operation(summary = "Gets all transactions")
//    @TransactionReadPermission
//    @GetMapping("")
//    @ResponseStatus(HttpStatus.OK)
//    public Flux<TransactionDto> getAll() {
//        return transactionFacade.getAll().map(TransactionMapper.INSTANCE::transactionToTransactionDto);
//    }
//
//    @Operation(summary = "Gets all transactions")
//    @TransactionReadPermission
//    @GetMapping("source/{source}")
//    @ResponseStatus(HttpStatus.OK)
//    public Flux<TransactionDto> getAllBySource(@PathVariable("source") @NonNull String source) {
//        return transactionFacade.getAllBySource(source).map(TransactionMapper.INSTANCE::transactionToTransactionDto);
//    }
//
//    @Operation(summary = "Gets transaction by complytId")
//    @TransactionReadPermission
//    @GetMapping("complytId/{complytId}")
//    @ResponseStatus(HttpStatus.OK)
//    public Mono<ResponseEntity<TransactionDto>> getByComplytId(@PathVariable("complytId") @NonNull UUID complytId) {
//        return transactionFacade.findByComplytId(complytId)
//                .map(transactionItem -> new ResponseEntity<>(TransactionMapper.INSTANCE.transactionToTransactionDto(transactionItem), HttpStatus.OK))
//                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
//    }
//
//    @Operation(summary = "This will update the transaction if found by externalId & source, otherwise it will create it")
//    @TransactionUpdatePermission
//    @PutMapping("source/{source}/externalId/{externalId}")
//    @ResponseStatus(HttpStatus.OK)
//    public Mono<ResponseEntity<TransactionDto>> upsert(@PathVariable("externalId") @NonNull String externalId, @PathVariable("source") @NonNull String source,
//                                                       @RequestBody @NonNull TransactionDto transactionDto) {
//        log.debug("Upsert transaction - DTO received in request body : " + transactionDto);
//        Transaction receivedTransaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto);
//
//        return transactionFacade.findByExternalIdAndSource(externalId, source)
//                .flatMap(originalTransaction -> transactionFacade.updateIfModified(externalId, source, receivedTransaction, originalTransaction))
//                .map(updatedTransaction -> ResponseEntity.status(HttpStatus.OK).body(TransactionMapper.INSTANCE.transactionToTransactionDto(updatedTransaction)))
//                .switchIfEmpty(transactionFacade.saveTransaction(receivedTransaction)
//                        .map(transaction -> ResponseEntity.status(HttpStatus.CREATED).body(TransactionMapper.INSTANCE.transactionToTransactionDto(transaction))));
//    }
//
//    @Operation(summary = "Marks the transaction as cancelled")
//    @TransactionDeletePermission
//    @DeleteMapping("source/{source}/externalId/{externalId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public Mono<ResponseEntity> delete(@PathVariable("externalId") @NonNull String externalId, @PathVariable("source") @NonNull String source) {
//        log.debug("Delete transaction - external id and source received as path variable : " + externalId + ", " + source);
//
//        return transactionFacade.markAsCancelled(externalId, source).log().map(transaction -> ResponseEntity.noContent().build());
//    }
//}