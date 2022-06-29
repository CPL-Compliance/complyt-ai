package com.complyt.v1.controllers;

import com.complyt.facades.TransactionFacade;
import com.complyt.security.permissions.transaction.TransactionDeletePermission;
import com.complyt.security.permissions.transaction.TransactionReadPermission;
import com.complyt.security.permissions.transaction.TransactionUpdatePermission;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.model.TransactionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Log
@Tag(name = "Transaction", description = "This is the Transaction controller")
@RestController
@RequestMapping(TransactionController.BASE_URL)
public class TransactionController {
    public static final String BASE_URL = "/v1/transactions";

    @NonNull
    private final TransactionFacade transactionFacade;

    @Operation(summary = "Gets transaction by externalId")
    @TransactionReadPermission
    @GetMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<TransactionDto>> getOne(@PathVariable("externalId") @NonNull String externalId) {
        return transactionFacade.findByExternalId(externalId)
                .map(transactionItem -> new ResponseEntity<>(TransactionMapper.INSTANCE.transactionToTransactionDto(transactionItem), HttpStatus.OK))
                .switchIfEmpty(Mono.error(new NotFoundException(externalId)));
    }

    @Operation(summary = "Gets all transactions")
    @TransactionReadPermission
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<TransactionDto> getAll() {
        return transactionFacade.getAll().map(transaction -> TransactionMapper.INSTANCE.transactionToTransactionDto(transaction));
    }

    @Operation(summary = "This will update the transaction if found by externalId, otherwise it will create it")
    @TransactionUpdatePermission
    @PutMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<TransactionDto>> update(@PathVariable("externalId") @NonNull String externalId,
                                                       @RequestBody @NonNull TransactionDto transactionDto) {
        return transactionFacade.upsert(externalId, TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto))
                .map(transaction -> ResponseEntity.ok().body(TransactionMapper.INSTANCE.transactionToTransactionDto(transaction)));
    }

    @Operation(summary = "This will calculate Sales Tax and update the Transaction by the External ID")
    @TransactionUpdatePermission
    @PutMapping("{externalId}/salesTax")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<TransactionDto>> updateSalesTax(@PathVariable("externalId") @NonNull String externalId) {
        return transactionFacade.updateSalesTax(externalId)
                .map(transaction -> ResponseEntity.ok().body(TransactionMapper.INSTANCE.transactionToTransactionDto(transaction)))
                .switchIfEmpty(Mono.error(new NotFoundException(externalId)));
    }

    @Operation(summary = "Marks the transaction as cancelled")
    @TransactionDeletePermission
    @DeleteMapping("{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity> delete(@PathVariable("externalId") @NonNull String externalId) {
        return transactionFacade.markAsCancelled(externalId).map(transaction -> ResponseEntity.noContent().build());
    }
}