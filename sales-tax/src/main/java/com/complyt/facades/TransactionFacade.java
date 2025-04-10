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
import com.complyt.utils.StringChecker;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.CustomerNotFoundApiException;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
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
                .flatMap(transactionNoComplytId -> Mono.just(transactionNoComplytId.getCustomer())
                        .flatMap(customer -> transactionService.injectDataToNewTransaction(transactionNoComplytId)
                                .flatMap(transactionWithData -> findSalesTaxTrackingByTransaction(transactionWithData)
                                .flatMap(salesTaxTracking -> nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)
                                        .flatMap(salesTaxTrackingWithNexusInfo -> transactionService.injectDataBySalesTaxTracking(transactionWithData, salesTaxTrackingWithNexusInfo)
                                                .flatMap(transactionWithInjectedDate -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                                        handleSalesTaxCalculationAndSave(transactionWithInjectedDate, salesTaxTrackingWithNexusInfo, customer)
                                                                .flatMap(returnedTransaction -> salesTaxTrackingService.handleSalesTaxEnforcement(returnedTransaction.setCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking())
                                                                        .thenReturn(returnedTransaction)) :
                                                        transactionService.calculateTotalAmounts(transactionWithInjectedDate)
                                                                .flatMap(transactionService::injectExchangeRateIfNeeded)
                                                                .flatMap(returnedTransaction -> transactionService.isTransactionWithStatusCancelled(transactionWithInjectedDate) ?
                                                                        transactionService.save(returnedTransaction.setCustomer(null))
                                                                                .map(savedTransaction -> savedTransaction.setCustomer(customer)) :
                                                                        saveAndHandleNexusTrackingCalculation(returnedTransaction.setCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking())))
                                                .map(savedTransaction -> savedTransaction.setCustomer(customer))
                                                .flatMap(savedTransactionWithCustomer -> transactionService.isTransactionWithStatusCancelled(savedTransactionWithCustomer) ? Mono.empty() : Mono.just(savedTransactionWithCustomer)))))));
    }


    private Mono<Transaction> handleSalesTaxCalculationAndSave(Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo, Customer customer) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), customer)
                .flatMap(transactionService::calculateTotalAmounts)
                .flatMap(transactionWithSalesTax -> transactionService.injectExchangeRateIfNeeded(transactionWithSalesTax))
                .flatMap(transactionService::save);
    }

    private Mono<Transaction> saveAndHandleNexusTrackingCalculation(Transaction transaction, SalesTaxTracking salesTaxTracking) {
        Customer transactionCustomer = transaction.getCustomer();

        return transactionService.save(transaction.setCustomer(null))
                .map(savedTransaction -> savedTransaction.setCustomer(transactionCustomer))
                .flatMap(savedTransaction -> salesTaxTrackingService.handleSalesTaxEnforcement(savedTransaction, salesTaxTracking)
                        .thenReturn(savedTransaction));
    }

    public Mono<Transaction> update(@NonNull String externalId, @NonNull String source, @NonNull Transaction modifiedTransaction, @NonNull Transaction originalTransaction) {
        return transactionService.checkComplytIdOfModifiedEqualsToOriginal(modifiedTransaction, originalTransaction)
                .flatMap(checkedModifiedTransaction -> Mono.just(checkedModifiedTransaction.getCustomer())
                        .flatMap(customer -> transactionService.injectDataToExistingTransaction(checkedModifiedTransaction, originalTransaction)
                                .flatMap(transactionWithData -> findSalesTaxTrackingByTransaction(transactionWithData)
                                        .flatMap(salesTaxTracking -> nexusService.salesTaxTrackingWithNexusIndication(salesTaxTracking)
                                                .flatMap(salesTaxTrackingWithNexusInfo -> transactionService.injectDataBySalesTaxTracking(transactionWithData, salesTaxTrackingWithNexusInfo)
                                                        .flatMap(transactionWithInjectedDate -> salesTaxTrackingWithNexusInfo.isHasNexus() ?
                                                        handleSalesTaxCalculationAndUpdate(externalId, source, transactionWithInjectedDate, salesTaxTrackingWithNexusInfo, customer)
                                                                .flatMap(returnedTransaction -> salesTaxTrackingService.handleSalesTaxEnforcement(returnedTransaction.setCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking())
                                                                        .thenReturn(returnedTransaction)) :
                                                        transactionService.calculateTotalAmounts(transactionWithInjectedDate)
                                                                .flatMap(transactionService::injectExchangeRateIfNeeded)
                                                                .flatMap(returnedTransaction -> transactionService.isTransactionWithStatusCancelled(transactionWithInjectedDate) ?
                                                                        updateAndRemoveTransactionFromNexusTrackingCalculationIfNeeded(externalId, source, returnedTransaction, originalTransaction) :
                                                                        updateAndHandleNexusTrackingCalculation(externalId, source, returnedTransaction.setCustomer(customer), salesTaxTrackingWithNexusInfo.getSalesTaxTracking()))
                                                                .map(receivedTransaction -> receivedTransaction.setCustomer(customer))
                                                                .flatMap(receivedTransactionWithCustomer -> (transactionService.shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(receivedTransactionWithCustomer, originalTransaction) ?
                                                                        removeTransactionFromNexusTracking(originalTransaction) :
                                                                        Mono.just(receivedTransactionWithCustomer))
                                                                        .then(transactionService.isTransactionWithStatusCancelled(receivedTransactionWithCustomer) ? Mono.empty() : Mono.just(receivedTransactionWithCustomer)))))))));
    }

    private Mono<Transaction> handleSalesTaxCalculationAndUpdate(String externalId, String source, Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo, Customer customer) {
        return salesTaxService.handleSalesTaxCalculation(transaction, salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), customer)
                .flatMap(transactionService::calculateTotalAmounts)
                .flatMap(transactionWithSalesTax -> transactionService.injectExchangeRateIfNeeded(transactionWithSalesTax))
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
        return findByExternalIdAndSource(externalId, source, true);
    }

    public Mono<Transaction> findByExternalIdAndSource(String externalId, String source, boolean detailed) {
        return detailed ? transactionService.findByExternalIdAndSource(externalId, source)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::setCustomer)) :
                transactionService.findByExternalIdAndSourceProjection(externalId, source)
                        .flatMap(transaction -> getCustomerProjectionByTransaction(transaction)
                                .map(transaction::setCustomer));
    }

    public Mono<Transaction> findByComplytId(@NonNull UUID complytId) {
        return transactionService.findByComplytId(complytId)
                .flatMap(transaction -> getCustomerByTransaction(transaction)
                        .map(transaction::setCustomer));
    }

    public Flux<Transaction> getAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        return getAll(page, size, filterMap, sortOrder, sortBy, true);
    }

    public Flux<Transaction> getAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy, boolean detailed) {
        return detailed ? transactionService.findAll(page, size, filterMap, sortOrder, sortBy) :
                transactionService.findAllProjection(page, size, filterMap, sortOrder, sortBy);
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
                .switchIfEmpty(Mono.error(CustomerNotFoundApiException::new));
    }

    public Mono<Customer> getCustomerProjectionByTransaction(Transaction transaction) {
        return customerService.findByComplytIdProjection(transaction.getCustomerId())
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
                .switchIfEmpty(ContextLogger.observeCtx("ObjectNotFoundApiException thrown in TransactionFacade.findSalesTaxTrackingByTransaction because could not find SalesTaxTracking by state " + transaction.getShippingAddress().state() + " and subsidiary " + transaction.getSubsidiary() + " or null", log::error)
                        .then(Mono.error(new ObjectNotFoundApiException())));

    }


    public Mono<Customer> determineCustomerForNewTransaction(final UUID complytId,
                                                             final String externalReference,
                                                             final String source){
        if (complytId != null){
            return customerService.findByComplytId(complytId);
        } else if (StringChecker.isInputValid(externalReference, source)) {
            return customerService.findByExternalIdAndSource(externalReference, source);
        } else {
            return Mono.error(CustomerNotFoundApiException::new);
        }
    }
}