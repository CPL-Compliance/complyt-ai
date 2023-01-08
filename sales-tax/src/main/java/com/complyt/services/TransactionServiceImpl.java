package com.complyt.services;

import com.complyt.business.transaction.CountyProvider;
import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewTransactionInternalTimestampsInjector;
import com.complyt.business.transaction.items_amounts.TransactionAmountsCollector;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.repositories.TransactionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionServiceImpl implements TransactionService {

    @NonNull
    TransactionRepository transactionRepository;

    @NonNull
    @Qualifier("productClassificationServiceImpl")
    ProductClassificationService productClassificationService;

    @NonNull
    @Qualifier("transactionItemsAmountsCollector")
    TransactionAmountsCollector<Transaction> transactionAmountsCollector;

    @NonNull
    private CountyProvider countyProvider;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Transaction> findByExternalId(@NonNull String externalId) {
        return transactionRepository.findByExternalId(externalId);
    }

    public Mono<Transaction> update(@NonNull final String externalId, @NonNull final Transaction transaction) {
        return transactionRepository.findByExternalId(externalId)
                .switchIfEmpty(Mono.error(new NotFoundException("No Transaction with externalId " + externalId)))
                .map(createFunctionUpdateTransaction(transaction))
                .flatMap(transactionRepository::save);
    }

    @Override
    public Mono<Transaction> injectDataToModifiedTransaction(@NonNull Transaction modifiedTransaction, @NonNull Transaction originalTransaction) {
        Transaction newTransactionWithInternalTimestamps = modifiedTransaction
                .withInternalTimestamps(originalTransaction.getInternalTimestamps());

        return injectCommonDataToNewAndModifiedTransaction(newTransactionWithInternalTimestamps)
                .map(ExistingTransactionInternalTimestampsInjector::new)
                .map(ExistingTransactionInternalTimestampsInjector::inject);
    }

    @Override
    public Mono<Transaction> injectDataToNewTransaction(@NonNull Transaction transaction) {
        return injectCommonDataToNewAndModifiedTransaction(transaction)
                .map(NewTransactionInternalTimestampsInjector::new)
                .map(NewTransactionInternalTimestampsInjector::inject);
    }

    private Mono<Transaction> injectCommonDataToNewAndModifiedTransaction(Transaction transaction) {
        return productClassificationService.getTransactionWithRelevantProductClassificationData(transaction)
                .flatMap(countyProvider::provide)
                .map(transactionAmountsCollector::collect);
    }

    @Override
    public Mono<Transaction> findById(String id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Transaction> markAsCancelled(@NonNull String externalId) {
        return transactionRepository
                .findByExternalId(externalId)
                .map(transaction -> transaction.withTransactionStatus(TransactionStatus.CANCELLED))
                .flatMap(transactionRepository::save);
    }

    @Override
    public Flux<Transaction> getTransactionsByQuery(@NonNull Query query) {
        return transactionRepository.findAllByQuery(query);
    }

    public Flux<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    private Function<Transaction, Transaction> createFunctionUpdateTransaction(final Transaction transaction) {
        return transactionInfo -> transactionInfo
                .withExternalId(transaction.getExternalId())
                .withItems(transaction.getItems())
                .withBillingAddress(transaction.getBillingAddress())
                .withShippingAddress(transaction.getShippingAddress())
                .withCustomerId(transaction.getCustomerId())
                .withCustomer(transaction.getCustomer())
                .withSalesTax(transaction.getSalesTax())
                .withTransactionStatus(transaction.getTransactionStatus())
                .withInternalTimestamps(transaction.getInternalTimestamps())
                .withExternalTimestamps(transaction.getExternalTimestamps())
                .withTransactionType(transaction.getTransactionType())
                .withShippingFee(transaction.getShippingFee());
    }

}