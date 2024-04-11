package com.complyt.services;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewTransactionInternalTimestampsInjector;
import com.complyt.business.transaction.CityCountyProvider;
import com.complyt.business.transaction.ItemsTotalCalculator;
import com.complyt.business.transaction.items_amounts.TransactionAmountsCollector;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionStatus;
import com.complyt.repositories.TransactionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionServiceImpl implements TransactionService {

    @NonNull
    TransactionRepository transactionRepository;

    @NonNull
    ProductClassificationService productClassificationServiceImpl;

    @NonNull
    TransactionAmountsCollector<Transaction> transactionItemsAmountsCollector;

    @NonNull
    TransactionAmountsCollector<Transaction> transactionDiscountCollector;

    @NonNull
    CityCountyProvider cityCountyProvider;

    @NonNull
    private ComplytIdHandler<Transaction> complytIdHandler;

    @NonNull
    ItemsTotalCalculator itemsTotalCalculator;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Transaction> checkTransactionNotHavingComplytId(@NonNull final Transaction newTransaction) {
        return complytIdHandler.checkNewDontHaveComplytId(newTransaction);
    }

    @Override
    public Mono<Transaction> findByExternalIdAndSource(@NonNull String externalId, String source) {
        return transactionRepository.findByExternalIdAndSource(externalId, source);
    }

    @Override
    public Mono<Transaction> findByComplytId(@NonNull UUID complytId) {
        return transactionRepository.findByComplytId(complytId);
    }

    public Mono<Transaction> update(@NonNull final String externalId, @NonNull String source, @NonNull final Transaction transaction) {
        return transactionRepository.findByExternalIdAndSource(externalId, source)
                .switchIfEmpty(Mono.error(new NotFoundException("No Transaction with externalId: " + externalId + ", in source: " + source)))
                .map(createFunctionUpdateTransaction(transaction))
                .flatMap(transactionRepository::save);
    }

    @Override
    public Mono<Transaction> checkComplytIdOfModifiedEqualsToOriginal(@NonNull final Transaction modifiedTransaction, @NonNull final Transaction originalTransaction) {
        return complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(modifiedTransaction, originalTransaction);
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
                .map(x -> complytIdHandler.insertComplytIdToNew(x))
                .map(y -> new NewTransactionInternalTimestampsInjector(y))
                .map(z -> z.inject());
    }

    private Mono<Transaction> injectCommonDataToNewAndModifiedTransaction(Transaction transaction) {
        return itemsTotalCalculator.injectRecalculatedTotal(transaction)
                .flatMap(transactionWithCalculatedItems ->
                        productClassificationServiceImpl.getTransactionWithRelevantProductClassificationData(transactionWithCalculatedItems)
                                .map(x -> transactionDiscountCollector.collect(x))
                                .map(y -> transactionItemsAmountsCollector.collect(y))
                                .flatMap(transactionWithAmounts -> CountryIsUsaChecker.isCountryUsa(transactionWithAmounts.getShippingAddress()) ? //todo: make sure addressInUsaChecker is needed here is is the right thing to do here
                                        cityCountyProvider.provide(transactionWithAmounts) :
                                        Mono.just(transactionWithAmounts))); //todo: fix - this causes error if not usa state
    } //todo: also - county can be null, fails is not null

    @Deprecated
    @Override
    public Mono<Transaction> findById(@NonNull String id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Transaction> markAsCancelled(@NonNull String externalId, @NonNull String source) {
        return transactionRepository
                .findByExternalIdAndSource(externalId, source)
                .map(transaction -> transaction
                        .withTransactionStatus(TransactionStatus.CANCELLED)
                        .withCustomer(null))
                .flatMap(transactionRepository::save);
    }

    @Override
    public Flux<Transaction> getTransactionsByQuery(@NonNull Query query) {
        return transactionRepository.findAllByQuery(query);
    }


    public Flux<Transaction> findAll(int page, int size) {
        return transactionRepository.findAll(page, size);
    }


    public Flux<Transaction> findAllBySource(@NonNull final String source) {
        return transactionRepository.findAllBySource(source);
    }

    private Function<Transaction, Transaction> createFunctionUpdateTransaction(final Transaction transaction) {
        return transactionInfo ->
                new Transaction(
                        transactionInfo.getComplytId(), transactionInfo.getId(),
                        transaction.getExternalId(), transaction.getSource(), transaction.getDocumentName(),
                        transaction.getItems(), transaction.getIsTaxInclusive(), transaction.getBillingAddress(), transaction.getShippingAddress(), //todo: note now it's getIsTaxFromTotal
                        transaction.getCustomerId(), null, transaction.getSalesTax(),
                        transaction.getTransactionStatus(), transactionInfo.getTenantId(), transaction.getInternalTimestamps(),
                        transaction.getExternalTimestamps(), transaction.getTransactionType(), transaction.getShippingFee(),
                        transaction.getCreatedFrom(), transaction.getTaxableItemsAmount(),
                        transaction.getTangibleItemsAmount(), transaction.getTotalItemsAmount(), transaction.getFinalTransactionAmount(), transaction.getTotalDiscount(),
                        transaction.getTransactionFilingStatus(), transaction.getCurrency(),
                        transaction.getSubsidiary()
                );
    }

}