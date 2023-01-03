package com.complyt.facades;

import com.complyt.domain.Transaction;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
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
                        .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                handleSalesTaxCalculationAndSave(setTransaction, salesTaxTrackingWithNexusInfo) :
                                saveAndHandleNexusTrackingCalculation(setTransaction)));
    }

    private Mono<Transaction> handleSalesTaxCalculationAndSave(Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking())
                .flatMap(transactionService::save);
    }

    private Mono<Transaction> saveAndHandleNexusTrackingCalculation(Transaction transaction) {
        return nexusService.isNexusTrackingCalculationRequired(transaction) ? transactionService.save(transaction)
                .flatMap(savedTransaction -> nexusService.calculateNexusTracking(savedTransaction)
                        .thenReturn(savedTransaction)) : transactionService.save(transaction);
    }

    public Mono<Transaction> updateIfModified(@NonNull String externalId, @NonNull Transaction newTransaction, @NonNull Transaction originalTransaction) {
        return originalTransaction.equals(newTransaction) ?
                Mono.just(newTransaction) : update(externalId, newTransaction, originalTransaction);
    }

    public Mono<Transaction> update(@NonNull String externalId, @NonNull Transaction modifiedTransaction, @NonNull Transaction originalTransaction) {
        return transactionService.injectDataToModifiedTransaction(modifiedTransaction, originalTransaction)
                .flatMap(setTransaction -> nexusService.hasNexus(setTransaction)
                        .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                handleSalesTaxCalculationAndUpdate(externalId, setTransaction, salesTaxTrackingWithNexusInfo) :
                                updateAndHandleNexusTrackingCalculation(externalId, setTransaction)));
    }

    private Mono<Transaction> handleSalesTaxCalculationAndUpdate(String externalId, Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking())
                .flatMap(updatedTransaction -> transactionService.update(externalId, updatedTransaction));
    }

    private Mono<Transaction> updateAndHandleNexusTrackingCalculation(String externalId, Transaction transaction) {
        return nexusService.isNexusTrackingCalculationRequired(transaction) ? transactionService.update(externalId, transaction)
                .flatMap(updatedTransaction -> nexusService.calculateNexusTracking(updatedTransaction)
                        .thenReturn(updatedTransaction)) : transactionService.update(externalId, transaction);
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