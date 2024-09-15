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
    @Qualifier("salesTaxTrackingServiceImpl")
    private SalesTaxTrackingService salesTaxTrackingService;

    @NonNull
    private NexusService nexusService;

    public Mono<Transaction> saveTransaction(Transaction transaction) {
        return transactionService.checkTransactionNotHavingComplytId(transaction)
                .flatMap(checkedTransaction -> getCustomerByTransaction(transaction)
                        .flatMap(customer -> transactionService.injectDataToTransaction(checkedTransaction)
                                .flatMap(setTransaction -> findSalesTaxTrackingByTransaction(setTransaction)
                                        .flatMap(salesTaxTracking -> nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)
                                                .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                                        handleSalesTaxCalculationAndSave(setTransaction, salesTaxTrackingWithNexusInfo, customer)
                                                                .flatMap(returnedTransaction -> salesTaxTrackingService.handleSalesTaxEnforcement(returnedTransaction.setCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking())
                                                                        .thenReturn(returnedTransaction)) :
                                                        transactionService.isTransactionWithStatusCancelled(setTransaction) ?
                                                                transactionService.save(setTransaction.setCustomer(null))
                                                                        .map(savedTransaction -> savedTransaction.setCustomer(customer)) :
                                                                saveAndHandleNexusTrackingCalculation(setTransaction.setCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking()))
                                                .map(savedTransaction -> savedTransaction.setCustomer(customer))
                                                .flatMap(savedTransactionWithCustomer -> transactionService.isTransactionWithStatusCancelled(savedTransactionWithCustomer) ? Mono.empty() : Mono.just(savedTransactionWithCustomer))))));
    }

    private Mono<Transaction> handleSalesTaxCalculationAndSave(Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo, Customer customer) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), customer)
                .flatMap(transactionService::save);
    }

    private Mono<Transaction> saveAndHandleNexusTrackingCalculation(Transaction transaction, SalesTaxTracking salesTaxTracking) {
        Customer transactionCustomer = transaction.getCustomer();
        return transactionService.save(transaction.setCustomer(null))
                .map(savedTransaction -> savedTransaction.setCustomer(transactionCustomer))
                .flatMap(savedTransaction -> salesTaxTrackingService.handleSalesTaxEnforcement(savedTransaction, salesTaxTracking).thenReturn(savedTransaction));
    }

    public Mono<Transaction> update(@NonNull String externalId, @NonNull String source, @NonNull Transaction modifiedTransaction, @NonNull Transaction originalTransaction) {
        return transactionService.checkComplytIdOfModifiedEqualsToOriginal(modifiedTransaction, originalTransaction)
                .flatMap(checkedModifiedTransaction -> getCustomerByTransaction(modifiedTransaction)
                        .flatMap(customer -> transactionService.injectDataToTransaction(checkedModifiedTransaction, originalTransaction)
                                .flatMap(setTransaction -> findSalesTaxTrackingByTransaction(setTransaction)
                                        .flatMap(salesTaxTracking -> nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)
                                                .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                                        handleSalesTaxCalculationAndUpdate(externalId, source, setTransaction, salesTaxTrackingWithNexusInfo, customer)
                                                                .flatMap(returnedTransaction -> salesTaxTrackingService.handleSalesTaxEnforcement(returnedTransaction.setCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking())
                                                                        .thenReturn(returnedTransaction)) :
                                                        transactionService.isTransactionWithStatusCancelled(setTransaction) ?
                                                                updateAndRemoveTransactionFromNexusTrackingCalculationIfNeeded(externalId, source, setTransaction, originalTransaction) :
                                                                updateAndHandleNexusTrackingCalculation(externalId, source, setTransaction.setCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking()))
                                                .map(receivedTransaction -> receivedTransaction.setCustomer(customer))
                                                .flatMap(receivedTransactionWithCustomer -> (transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(receivedTransactionWithCustomer, originalTransaction) ?
                                                        removeTransactionFromNexusTracking(originalTransaction) :
                                                        Mono.just(receivedTransactionWithCustomer))
                                                        .then(transactionService.isTransactionWithStatusCancelled(receivedTransactionWithCustomer) ? Mono.empty() : Mono.just(receivedTransactionWithCustomer)))))));
    }

    private Mono<Transaction> handleSalesTaxCalculationAndUpdate(String externalId, String source, Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo, Customer customer) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), customer)
                .flatMap(updatedTransaction -> transactionService.update(externalId, source, updatedTransaction));
    }

    private Mono<Transaction> updateAndHandleNexusTrackingCalculation(String externalId, String source, Transaction transaction, SalesTaxTracking salesTaxTracking) {
        return salesTaxTracking.isEnforcesSalesTax()
                ? transactionService.update(externalId, source, transaction)
                .flatMap(updatedTransaction -> nexusService.upsertToNexusTracking(updatedTransaction.setCustomer(transaction.getCustomer()), salesTaxTracking)
                        .flatMap(salesTaxTrackingAfterCalculation -> salesTaxTrackingService.handleSalesTaxTrackingAfterTransactionCalculated(salesTaxTrackingAfterCalculation))
                        .thenReturn(updatedTransaction))
                : transactionService.update(externalId, source, transaction);
    }

    // The transaction is removed form the Nexus tracking calculation if the transaction status just changed to CANCELLED
    // Always returning Mono.empty() to keep the output aligned with the 'delete transaction' to return 204 when status is CANCELLED
    private Mono<Transaction> updateAndRemoveTransactionFromNexusTrackingCalculationIfNeeded(String externalId, String source, Transaction modifiedTransaction, Transaction originalTransaction) {
        return transactionService.update(externalId, source, modifiedTransaction)
                .map(updatedTransaction -> updatedTransaction.setCustomer(modifiedTransaction.getCustomer()))
                .flatMap(updatedTransactionWithCustomer -> transactionService.hasModifiedTransactionStatusChangedToCancelled(updatedTransactionWithCustomer, originalTransaction) ?
                        removeTransactionFromNexusTracking(updatedTransactionWithCustomer) :
                        Mono.empty());
    }

    public Mono<Transaction> findByExternalIdAndSource(String externalId, String source) {
        return transactionService.findByExternalIdAndSource(externalId, source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::setCustomer));
    }

    public Mono<Transaction> findByComplytId(@NonNull UUID complytId) {
        return transactionService.findByComplytId(complytId)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::setCustomer));
    }

    public Flux<Transaction> getAll(int page, int size) {
        return transactionService.findAll(page, size);
    }

    public Flux<Transaction> getAllBySource(String source) {
        return transactionService.findAllBySource(source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::setCustomer));
    }

    public Mono<Transaction> markAsCancelled(String externalId, String source) {
        return transactionService.markAsCancelled(externalId, source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::setCustomer))
                .flatMap(transaction -> findSalesTaxTrackingByTransaction(transaction)
                        .flatMap(salesTaxTracking -> nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking))
                        .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ? Mono.empty() :
                                nexusService.removeFromNexusTracking(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking()).flatMap(salesTaxTrackingService::save))
                        .thenReturn(transaction));
    }

    public Mono<Customer> getCustomerByTransaction(Transaction transaction) {
        return customerService.findByComplytId(transaction.getCustomerId())
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
    }

    public Mono<Transaction> removeTransactionFromNexusTracking(Transaction transaction) {
        return findSalesTaxTrackingByTransaction(transaction)
                .flatMap(salesTaxTracking -> nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking))
                .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingWithNexusInfo.isHasNexus() ? Mono.empty() :
                        nexusService.removeFromNexusTracking(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking()).flatMap(salesTaxTrackingService::save))
                .thenReturn(transaction);
    }

    public Mono<SalesTaxTracking> findSalesTaxTrackingByTransaction(@NonNull Transaction transaction) {
        return salesTaxTrackingService.findByCountryStateAndSubsidiary(transaction.getShippingAddress().country(), transaction.getShippingAddress().state(), transaction.getSubsidiary())
                .switchIfEmpty(salesTaxTrackingService.findByCountryStateAndSubsidiary(transaction.getShippingAddress().country(), transaction.getShippingAddress().state(), null))
                .switchIfEmpty(Mono.error(ObjectNotFoundApiException::new));

    }
}