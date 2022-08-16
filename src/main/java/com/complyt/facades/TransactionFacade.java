package com.complyt.facades;

import com.complyt.domain.Transaction;
import com.complyt.services.SalesTaxService;
import com.complyt.services.TransactionService;
import com.complyt.services.nexus.NexusService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Component
public class TransactionFacade {

    @NonNull
    @Qualifier("transactionServiceImpl")
    private TransactionService transactionService;

    @NonNull
    @Qualifier("salesTaxServiceImpl")
    private SalesTaxService salesTaxService;

    @NonNull
    private NexusService nexusService;

    public Mono<Transaction> saveTransaction(Transaction transaction) {
        return transactionService.injectDataToNewTransaction(transaction)
                .flatMap(setTransaction -> nexusService.hasNexus(setTransaction)
                        .flatMap(salesTaxTrackingDecorator -> salesTaxTrackingDecorator.isHasNexus() ?
                                salesTaxService.handleSalesTaxCalculation(setTransaction, salesTaxTrackingDecorator.getSalesTaxTracking()).flatMap(transactionService::save) :
                                transactionService.save(setTransaction).flatMap(nexusService::calculateNexusTracking).thenReturn(setTransaction)));
    }

    public Mono<Transaction> updateIfModified(@NonNull String externalId, @NonNull Transaction newTransaction, @NonNull Transaction oldTransaction) {
        return oldTransaction.equals(newTransaction) ?
                Mono.just(newTransaction) : update(externalId, newTransaction, oldTransaction);
    }

    public Mono<Transaction> update(@NonNull String externalId, @NonNull Transaction modifiedTransaction, @NonNull Transaction oldTransaction) {
        return transactionService.injectDataToModifiedTransaction(modifiedTransaction, oldTransaction)
                .flatMap(setTransaction -> nexusService.hasNexus(setTransaction)
                        .flatMap(salesTaxTrackingDecorator -> salesTaxTrackingDecorator.isHasNexus() ?
                                salesTaxService.handleSalesTaxCalculation(setTransaction, salesTaxTrackingDecorator.getSalesTaxTracking()).flatMap(updatedTransaction -> transactionService.update(externalId, updatedTransaction)) :
                                transactionService.update(externalId, setTransaction).flatMap(nexusService::calculateNexusTracking).thenReturn(setTransaction)));
    }

    public Mono<Transaction> findByExternalId(String externalId) {
        return transactionService.findByExternalId(externalId);
    }

    public Flux<Transaction> getAll() {
        return transactionService.findAll();
    }

    public Mono<Transaction> markAsCancelled(String transactionId) {
        return transactionService.markAsCancelled(transactionId);
    }
}