package com.complyt.facades;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.services.CustomerService;
import com.complyt.services.SalesTaxService;
import com.complyt.services.TransactionService;
import com.complyt.services.nexus.NexusService;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Component
public class TransactionFacade {

    @NonNull
    @Qualifier("transactionServiceImpl")
    private TransactionService transactionService;

    @NonNull
    @Qualifier("customerServiceImpl")
    private CustomerService customerService;

    @NonNull
    @Qualifier("salesTaxServiceImpl")
    private SalesTaxService salesTaxService;

    @NonNull
    private NexusService nexusService;

    public Mono<Transaction> saveTransaction(Transaction transaction) {
        return transactionService.checkTransactionNotHavingComplytId(transaction)
                .flatMap(checkedTransaction -> getCustomerByTransaction(transaction)
                        .flatMap(customer -> transactionService.injectDataToNewTransaction(checkedTransaction)
                                .flatMap(setTransaction -> nexusService.hasNexus(setTransaction)
                                        .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                                handleSalesTaxCalculationAndSave(setTransaction, salesTaxTrackingWithNexusInfo, customer) :
                                                saveAndHandleNexusTrackingCalculation(setTransaction))
                                        .map(savedTransaction -> savedTransaction.withCustomer(customer)))));
    }

    private Mono<Transaction> handleSalesTaxCalculationAndSave(Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo, Customer customer) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), customer)
                .flatMap(transactionService::save);
    }

    private Mono<Transaction> saveAndHandleNexusTrackingCalculation(Transaction transaction) {
        return nexusService.isNexusTrackingCalculationRequired(transaction) ? transactionService.save(transaction)
                .flatMap(savedTransaction -> nexusService.calculateNexusTracking(savedTransaction)
                        .thenReturn(savedTransaction)) : transactionService.save(transaction);
    }

    public Mono<Transaction> updateIfModified(@NonNull String externalId, @NonNull String source, @NonNull Transaction newTransaction, @NonNull Transaction originalTransaction) {
        return originalTransaction.equals(newTransaction) ?
                Mono.just(newTransaction) : update(externalId, source, newTransaction, originalTransaction);
    }

    public Mono<Transaction> update(@NonNull String externalId, @NonNull String source, @NonNull Transaction modifiedTransaction, @NonNull Transaction originalTransaction) {
        return transactionService.checkComplytIdOfModifiedEqualsToOriginal(modifiedTransaction, originalTransaction)
                .flatMap(checkedModifiedTransaction -> getCustomerByTransaction(modifiedTransaction)
                        .flatMap(customer -> transactionService.injectDataToModifiedTransaction(checkedModifiedTransaction, originalTransaction)
                                .flatMap(setTransaction -> nexusService.hasNexus(setTransaction)
                                        .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                                handleSalesTaxCalculationAndUpdate(externalId, source, setTransaction, salesTaxTrackingWithNexusInfo, customer) :
                                                updateAndHandleNexusTrackingCalculation(externalId, source, setTransaction))
                                        .map(receivedTransaction -> receivedTransaction.withCustomer(customer)))));
    }

    private Mono<Transaction> handleSalesTaxCalculationAndUpdate(String externalId, String source, Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo, Customer customer) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), customer)
                .flatMap(updatedTransaction -> transactionService.update(externalId, source, updatedTransaction));
    }

    private Mono<Transaction> updateAndHandleNexusTrackingCalculation(String externalId, String source, Transaction transaction) {
        return nexusService.isNexusTrackingCalculationRequired(transaction) ? transactionService.update(externalId, source, transaction)
                .flatMap(updatedTransaction -> nexusService.calculateNexusTracking(updatedTransaction)
                        .thenReturn(updatedTransaction)) : transactionService.update(externalId, source, transaction);
    }

    public Mono<Transaction> findByExternalIdAndSource(String externalId, String source) {
        return transactionService.findByExternalIdAndSource(externalId, source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(customer -> transaction.withCustomer(customer)));
    }

    public Mono<Transaction> findByComplytId(@NonNull UUID complytId) {
        return transactionService.findByComplytId(complytId)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(customer -> transaction.withCustomer(customer)));
    }

    public Flux<Transaction> getAll() {
        return transactionService.findAll()
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(customer -> transaction.withCustomer(customer)));
    }

    public Flux<Transaction> getAllBySource(String source) {

        return transactionService.findAllBySource(source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(customer -> transaction.withCustomer(customer)));
    }

    public Mono<Transaction> markAsCancelled(String externalId, String source) {
        return transactionService.markAsCancelled(externalId, source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(customer -> transaction.withCustomer(customer)));
    }

    public Mono<Customer> getCustomerByTransaction(Transaction transaction) {
        return customerService.findByComplytId(transaction.getCustomerId())
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
    }
}