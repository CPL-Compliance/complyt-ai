package com.complyt.facades;

import com.complyt.domain.Transaction;
import com.complyt.services.CustomerService;
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

    @NonNull
    @Qualifier("customerServiceImpl")
    private CustomerService customerService;

    public Mono<Transaction> saveTransaction(Transaction transaction) {
        return customerService.findById(transaction.getCustomerId())
                .flatMap(customer -> transactionService.injectDataToNewTransaction(transaction, customer)
                        .flatMap(setTransaction -> nexusService.hasNexus(setTransaction)
                                .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                        salesTaxService.handleSalesTaxCalculation(setTransaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking()).flatMap(transactionService::save) :
                                        transactionService.save(setTransaction).flatMap(nexusService::calculateNexusTracking).thenReturn(setTransaction))));
    }

    public Mono<Transaction> updateIfModified(@NonNull String externalId, @NonNull Transaction newTransaction, @NonNull Transaction originalTransaction) {
        return originalTransaction.equals(newTransaction) ?
                Mono.just(newTransaction) : update(externalId, newTransaction, originalTransaction);
    }

    public Mono<Transaction> update(@NonNull String externalId, @NonNull Transaction modifiedTransaction, @NonNull Transaction originalTransaction) {
        return transactionService.injectDataToModifiedTransaction(modifiedTransaction, originalTransaction)
                .flatMap(setTransaction -> nexusService.hasNexus(setTransaction)
                        .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                salesTaxService.handleSalesTaxCalculation(setTransaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking()).flatMap(updatedTransaction -> transactionService.update(externalId, updatedTransaction)) :
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