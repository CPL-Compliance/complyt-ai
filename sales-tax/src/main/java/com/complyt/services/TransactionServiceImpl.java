package com.complyt.services;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.strategy.StrategySelector;
import com.complyt.business.strategy.currencyExchange.CurrenciesWebClientWrapper;
import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewTransactionInternalTimestampsInjector;
import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.business.transaction.CityCountyProvider;
import com.complyt.business.transaction.CurrencyProcessor;
import com.complyt.business.transaction.DiscountCalculator;
import com.complyt.business.transaction.items_amounts.TransactionAmountsCollector;
import com.complyt.domain.currency.CurrencyExchangeRateObject;
import com.complyt.domain.currency.CurrencySource;
import com.complyt.domain.transaction.ExchangeRateInfo;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionStatus;
import com.complyt.repositories.GeoRecordRepository;
import com.complyt.repositories.TransactionRepository;
import com.complyt.v1.exceptions.types.CurrencyNotFoundApiException;
import com.complyt.v1.exceptions.types.ZipCodeNotFoundApiException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionServiceImpl implements TransactionService {

    @NonNull
    TransactionRepository transactionRepository;

    @NonNull
    GeoRecordRepository geoRecordRepository;

    @NonNull
    ProductClassificationService productClassificationServiceImpl;

    @NonNull
    TransactionAmountsCollector<Transaction> transactionItemsAmountsCollector;

    @NonNull
    TransactionAmountsCollector<Transaction> finalTransactionAmountCollector;

    @NonNull
    TransactionAmountsCollector<Transaction> transactionDiscountCollector;

    @NonNull
    CityCountyProvider cityCountyProvider;

    @NonNull
    ComplytIdHandler<Transaction> complytIdHandler;

    @NonNull
    DiscountCalculator itemsDiscountCalculator;

    @NonNull
    DiscountCalculator transactionDiscountCalculator;

    @NonNull
    DiscountCalculator shippingFeeCalculator;

    @NonNull
    StrategySelector shippingAddressAlignmentStrategy;

    @NonNull
    CurrenciesWebClientWrapper currenciesWebClientWrapper;

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
    public Mono<Transaction> injectDataToTransaction(@NonNull Transaction modifiedTransaction, @NonNull Transaction originalTransaction) {
        Transaction newTransactionWithInternalTimestamps = modifiedTransaction
                .setInternalTimestamps(originalTransaction.getInternalTimestamps());

        return injectCommonDataToTransaction(newTransactionWithInternalTimestamps)
                .map(ExistingTransactionInternalTimestampsInjector::new)
                .map(ExistingTransactionInternalTimestampsInjector::inject);
    }

    @Override
    public Mono<Transaction> injectDataToTransaction(@NonNull Transaction transaction) {
        return injectCommonDataToTransaction(transaction)
                .map(complytIdHandler::insertComplytIdToNew)
                .map(NewTransactionInternalTimestampsInjector::new)
                .map(NewTransactionInternalTimestampsInjector::inject);
    }

    private Mono<Transaction> injectCommonDataToTransaction(Transaction transaction) {
        return injectStateIfMissingInPartialAddress(transaction)
                .flatMap(this::recalculateTotalItemsPrice)
                .map(transactionWithCalculatedItems ->
                        (Transaction) shippingAddressAlignmentStrategy.select(transaction).apply(transactionWithCalculatedItems))
                .flatMap(transactionWithCalculatedItemsAndShippingAddress ->
                        productClassificationServiceImpl.getTransactionWithRelevantProductClassificationData(transactionWithCalculatedItemsAndShippingAddress)
                                .map(finalTransactionAmountCollector::collect)
                                .flatMap(transactionWithAmounts -> CountryIsUsaChecker.isCountryUsa(transactionWithAmounts.getShippingAddress()) ?
                                        cityCountyProvider.provide(transactionWithAmounts) :
                                        Mono.just(transactionWithAmounts)));
    }

    // Apply the discounts on items
    private Mono<Transaction> recalculateTotalItemsPrice(Transaction transaction) {
        return itemsDiscountCalculator.injectRecalculatedTotalAfterDiscount(transaction)
                .flatMap(shippingFeeCalculator::injectRecalculatedTotalAfterDiscount)
                .flatMap(this::calculateTransactionLevelDiscountIfExist);
    }

    private Mono<Transaction> injectStateIfMissingInPartialAddress(Transaction transaction) {
        return CountryIsUsaChecker.isCountryUsa(transaction.getShippingAddress()) && (transaction.getShippingAddress().state() == null || transaction.getShippingAddress().state().isEmpty()) ?
                geoRecordRepository.findStateByZip(transaction.getShippingAddress().zip())
                        .map(geoRecord -> transaction.setShippingAddress(transaction.getShippingAddress().withState(geoRecord.getState())))
                        .switchIfEmpty(Mono.error(ZipCodeNotFoundApiException::new)) :
                Mono.just(transaction);
    }

    public Mono<Transaction> calculateTotalAmounts(Transaction transaction) {
        return Mono.just(transaction)
                .map(transactionDiscountCollector::collect)
                .map(transactionItemsAmountsCollector::collect);
    }

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
                        .setTransactionStatus(TransactionStatus.CANCELLED)
                        .setCustomer(null))
                .flatMap(transactionRepository::save);
    }

    @Override
    public Flux<Transaction> getTransactionsByQuery(@NonNull Query query) {
        return transactionRepository.findAllByQuery(query);
    }

    public Flux<Transaction> findAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        return transactionRepository.findAll(page, size, filterMap, sortOrder, sortBy);
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
                        transaction.getTransactionLevelDiscount(), transaction.getTransactionFilingStatus(), transaction.getCurrency(),
                        transaction.getRefRate(), transaction.getExchangeRateInfo(), transaction.getSubsidiary()
                );
    }

    public Boolean shouldRemoveTransactionFromOriginalNexusTrackingDueToChangeInCountryStateOrSubsidiary(@NonNull final Transaction modifiedTransaction, @NonNull final Transaction originalTransaction) {
        return !Objects.equals(modifiedTransaction.getShippingAddress().country(), originalTransaction.getShippingAddress().country()) ||
                !Objects.equals(modifiedTransaction.getShippingAddress().state(), originalTransaction.getShippingAddress().state()) ||
                !Objects.equals(modifiedTransaction.getSubsidiary(), originalTransaction.getSubsidiary());
    }

    public Boolean isTransactionWithStatusCancelled(@NonNull final Transaction transaction) {
        return transaction.getTransactionStatus() == TransactionStatus.CANCELLED;
    }

    public Boolean hasModifiedTransactionStatusChangedToCancelled(@NonNull final Transaction modifiedTransaction, @NonNull final Transaction originalTransaction) {
        return (originalTransaction.getTransactionStatus() == TransactionStatus.ACTIVE || originalTransaction.getTransactionStatus() == TransactionStatus.PAID) &&
                modifiedTransaction.getTransactionStatus() == TransactionStatus.CANCELLED;
    }

    private Mono<Transaction> calculateTransactionLevelDiscountIfExist(Transaction transaction) {
        return transaction.getTransactionLevelDiscount() != null && !transaction.getTransactionLevelDiscount().equals(BigDecimal.ZERO) ?
                transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transaction) :
                Mono.just(transaction);
    }

    @Override
    public Mono<Transaction> injectExchangeRateIfNeeded(@NonNull final Transaction transaction) {
        return Mono.justOrEmpty(transaction.getCurrency())
                .flatMap(currency -> CurrencyProcessor.alignCurrency(transaction))
                .map(Transaction::getCurrency)
                .filter(alignedCurrency -> !CurrencyProcessor.isUsdCurrency(alignedCurrency))
                .flatMap(alignedCurrency -> getCurrencyExchangeRate(transaction)
                        .onErrorResume(e -> Mono.error(CurrencyNotFoundApiException::new))
                        .flatMap(exchangeRate -> calculateExchangeRateInfo(transaction, exchangeRate)
                                .map(exchangeRateInfo -> {
                                    transaction.setExchangeRateInfo(exchangeRateInfo);
                                    return transaction;
                                })
                        )
                )
                .defaultIfEmpty(transaction);
    }

    private Mono<ExchangeRateInfo> calculateExchangeRateInfo(Transaction transaction, BigDecimal exchangeRate) {
        Boolean isFutureExternalCreatedDate = CurrencyProcessor.isFutureExternalCreatedDate(transaction);
        LocalDateTime exchangeRateDate = CurrencyProcessor.getExchangeRateDate(transaction);
        CurrencySource exchangeSource = CurrencyProcessor.getExchangeSource(transaction);

        BigDecimal transactionSalesTaxInUsd = transaction.getSalesTax() != null ?
                BigDecimalProcessor.removeTrailingZeros(transaction.getSalesTax().amount().multiply(exchangeRate)) :
                BigDecimal.ZERO;

        BigDecimal totalItemsAmountInUsd = transaction.getIsTaxInclusive() ?
                BigDecimalProcessor.removeTrailingZeros(transaction.getTotalItemsAmount().multiply(exchangeRate).subtract(transactionSalesTaxInUsd)) :
                BigDecimalProcessor.removeTrailingZeros(transaction.getTotalItemsAmount().multiply(exchangeRate));

        BigDecimal finalTransactionAmountInUsd = transaction.getIsTaxInclusive() ?
                BigDecimalProcessor.removeTrailingZeros(transaction.getFinalTransactionAmount().multiply(exchangeRate)) :
                totalItemsAmountInUsd.add(transactionSalesTaxInUsd);

        return Mono.just(new ExchangeRateInfo(totalItemsAmountInUsd, transactionSalesTaxInUsd, finalTransactionAmountInUsd, transaction.getCurrency(),
                CurrencyProcessor.usdCurrency, exchangeRate, exchangeSource, isFutureExternalCreatedDate, exchangeRateDate
        ));
    }

    private Mono<BigDecimal> getCurrencyExchangeRate(Transaction transaction) {
        return transaction.getRefRate() != null ?
                Mono.just(transaction.getRefRate()) :
                currenciesWebClientWrapper.getExchangeRateByCurrencyAndDate(transaction.getCurrency(), transaction.getExternalTimestamps().getCreatedDate())
                        .map(CurrencyExchangeRateObject::rate)
                        .map(BigDecimalProcessor::removeTrailingZeros);

    }
}