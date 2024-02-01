package com.complyt.facades;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.transaction.Transaction;
import com.complyt.services.CustomerService;
import com.complyt.services.SalesTaxService;
import com.complyt.services.TransactionService;
import com.complyt.services.nexus.NexusService;
import com.complyt.services.nexus.SalesTaxTrackingService;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
    @Qualifier("salesTaxTrackingServiceImpl")
    private SalesTaxTrackingService salesTaxTrackingService;

    @NonNull
    private NexusService nexusService;

    public Mono<Transaction> saveTransaction(Transaction transaction) {
        return transactionService.checkTransactionNotHavingComplytId(transaction)
                .flatMap(checkedTransaction -> getCustomerByTransaction(transaction)
                        .flatMap(customer -> transactionService.injectDataToNewTransaction(checkedTransaction)
                                .flatMap(setTransaction -> salesTaxTrackingService.findByState(setTransaction.getShippingAddress().state())
                                        .flatMap(salesTaxTracking -> nexusService.hasNexus(salesTaxTracking)
                                                .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus()
                                                        ? handleSalesTaxCalculationAndSave(setTransaction, salesTaxTrackingWithNexusInfo, customer)
                                                        : saveAndHandleNexusTrackingCalculation(setTransaction.withCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking()))
                                                .map(savedTransaction -> savedTransaction.withCustomer(customer))))))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
    }

    private Mono<Transaction> handleSalesTaxCalculationAndSave(Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo, Customer customer) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), customer)
                .flatMap(transactionService::save);
    }

    private Mono<Transaction> saveAndHandleNexusTrackingCalculation(Transaction transaction, SalesTaxTracking salesTaxTracking) {
        return transactionService.save(transaction.withCustomer(null))
                .map(savedTransaction -> savedTransaction.withCustomer(transaction.getCustomer()))
                .flatMap(savedTransaction -> salesTaxTracking.isEnforcesSalesTax()
                        ? nexusService.upsertToNexusTracking(savedTransaction, salesTaxTracking)
                        .flatMap(salesTaxTrackingService::save)
                        .thenReturn(savedTransaction)
                        : Mono.just(savedTransaction));
    }

    public Mono<Transaction> updateIfModified(@NonNull String externalId, @NonNull String source, @NonNull Transaction newTransaction, @NonNull Transaction originalTransaction) {
        return originalTransaction.equals(newTransaction) ?
                Mono.just(newTransaction) : update(externalId, source, newTransaction, originalTransaction);
    }

    public Mono<Transaction> update(@NonNull String externalId, @NonNull String source, @NonNull Transaction modifiedTransaction, @NonNull Transaction originalTransaction) {
        return transactionService.checkComplytIdOfModifiedEqualsToOriginal(modifiedTransaction, originalTransaction)
                .flatMap(checkedModifiedTransaction -> getCustomerByTransaction(modifiedTransaction)
                        .flatMap(customer -> transactionService.injectDataToModifiedTransaction(checkedModifiedTransaction, originalTransaction)
                                .flatMap(setTransaction -> salesTaxTrackingService.findByState(setTransaction.getShippingAddress().state())
                                        .flatMap(salesTaxTracking -> nexusService.hasNexus(salesTaxTracking)
                                                .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                                        handleSalesTaxCalculationAndUpdate(externalId, source, setTransaction, salesTaxTrackingWithNexusInfo, customer) :
                                                        updateAndHandleNexusTrackingCalculation(externalId, source, setTransaction.withCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking()))
                                                .map(receivedTransaction -> receivedTransaction.withCustomer(customer))))));
    }   

    private Mono<Transaction> handleSalesTaxCalculationAndUpdate(String externalId, String source, Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo, Customer customer) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), customer)
                .flatMap(updatedTransaction -> transactionService.update(externalId, source, updatedTransaction));
    }

    private Mono<Transaction> updateAndHandleNexusTrackingCalculation(String externalId, String source, Transaction transaction, SalesTaxTracking salesTaxTracking) {
        return salesTaxTracking.isEnforcesSalesTax()
                ? transactionService.update(externalId, source, transaction)
                .flatMap(updatedTransaction -> nexusService.upsertToNexusTracking(updatedTransaction.withCustomer(transaction.getCustomer()), salesTaxTracking)
                        .flatMap(salesTaxTrackingService::save)
                        .thenReturn(updatedTransaction))
                : transactionService.update(externalId, source, transaction);
    }

    public Mono<Transaction> findByExternalIdAndSource(String externalId, String source) {
        return transactionService.findByExternalIdAndSource(externalId, source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::withCustomer));
    }

    public Mono<Transaction> findByComplytId(@NonNull UUID complytId) {
        return transactionService.findByComplytId(complytId)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::withCustomer));
    }


        public Flux<Transaction> getAll(int page, int size) {
        return transactionService.findAll(page, size)
                .flatMapSequential(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::withCustomer));
    }

    public Flux<Transaction> getAllBySource(String source) {
        return transactionService.findAllBySource(source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::withCustomer));
    }

    public Mono<Transaction> markAsCancelled(String externalId, String source) {
        return transactionService.markAsCancelled(externalId, source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::withCustomer))
                .flatMap(transaction -> salesTaxTrackingService.findByState(transaction.getShippingAddress().state())
                        .flatMap(salesTaxTracking -> nexusService.hasNexus(salesTaxTracking))
                        .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ? Mono.empty() :
                                nexusService.removeFromNexusTracking(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking()).flatMap(salesTaxTrackingService::save))
                        .thenReturn(transaction));
    }

    public Mono<Customer> getCustomerByTransaction(Transaction transaction) {
        return customerService.findByComplytId(transaction.getCustomerId())
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
    }
}