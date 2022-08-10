package com.complyt.services;

import com.complyt.business.utils.date_injector.ModifiedTransactionInternalDateInjector;
import com.complyt.business.utils.date_injector.NewTransactionInternalDateInjector;
import com.complyt.business.utils.transaction_data_injector.CountyInjector;
import com.complyt.business.utils.transaction_data_injector.FastTaxCountyInjector;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
public class TransactionServiceImpl implements TransactionService {

    @NonNull
    private TransactionRepository transactionRepository;

    @NonNull
    @Qualifier("productClassificationServiceImpl")
    private ProductClassificationService productClassificationService;

    @NonNull
    private CountyInjector countyInjector;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Transaction> findByExternalId(@NonNull String externalId) {
        return transactionRepository.findByExternalId(externalId);
    }

    public Mono<Transaction> update(@NonNull final String externalId, @NonNull final Transaction transaction) {
        return transactionRepository.findByExternalId(externalId).log()
                .switchIfEmpty(Mono.error(new NotFoundException("No Transaction with externalId " + externalId)))
                .map(createUpdateTransactionFunction(transaction))
                .flatMap(transactionRepository::save);
    }

    @Override
    public Mono<Transaction> injectDataToModifiedTransaction(@NonNull Transaction newTransaction, @NonNull Transaction oldTransaction) {
        Transaction newTransactionWithInternalTimeStamps = newTransaction.withInternalTimeStamps(oldTransaction.getInternalTimeStamps());

        return productClassificationService.getTransactionWithRelevantProductClassificationData(newTransactionWithInternalTimeStamps)
                .flatMap(countyInjector::inject)
                .map(ModifiedTransactionInternalDateInjector::new)
                .map(ModifiedTransactionInternalDateInjector::inject);
    }

    @Override
    public Mono<Transaction> injectDataToNewTransaction(@NonNull Transaction transaction) {
        return productClassificationService.getTransactionWithRelevantProductClassificationData(transaction)
                .flatMap(countyInjector::inject)
                .map(NewTransactionInternalDateInjector::new)
                .map(NewTransactionInternalDateInjector::inject);
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

    private Function<Transaction, Transaction> createUpdateTransactionFunction(@NonNull final Transaction transaction) {
        return transactionInfo -> transactionInfo.withExternalId(transaction.getExternalId())
                .withItems(transaction.getItems())
                .withBillingAddress(transaction.getBillingAddress())
                .withShippingAddress(transaction.getShippingAddress())
                .withCustomerId(transaction.getCustomerId())
                .withSalesTax(transaction.getSalesTax())
                .withTransactionStatus(transaction.getTransactionStatus())
                .withInternalTimeStamps(transaction.getInternalTimeStamps())
                .withExternalTimeStamps(transaction.getExternalTimeStamps());
    }
}
