package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
<<<<<<< HEAD
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.factory.DateRange;
=======
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
>>>>>>> 1b610118 (merged main)
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

<<<<<<< HEAD
import java.math.BigDecimal;
=======
import java.time.LocalDateTime;
>>>>>>> 1b610118 (merged main)
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class NexusCalculator {

    @NonNull
    private NexusTransactionSummaryCalculator nexusTransactionSummaryCalculator;

    @NonNull
    private TransactionsFilterByNexusRules transactionsFilterByNexusRules;

<<<<<<< HEAD
    public Mono<SalesTaxTracking> calculateNexusSummary(@NonNull List<Transaction> transactions, @NonNull SalesTaxTracking salesTaxTracking, @NonNull DateRange summaryDateRange) {
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, salesTaxTracking.getNexusStateRule());

<<<<<<< HEAD
        return ContextLogger.observeCtx("Calculating amount and count for all transactions on timeframe : " + salesTaxTracking.getNexusStateRule().timeFrame(), log::debug)
                .thenMany(Flux.fromIterable(filteredTransactions)).flatMap(transaction -> nexusTransactionSummaryCalculator.extract(transaction, salesTaxTracking.getNexusStateRule())
                        .flatMap(transactionNexusSummary -> insertNewTransactionNexusSummary(salesTaxTracking, transaction.getComplytId(), transactionNexusSummary)))
                .map(transactionNexusSummary -> transactionNexusSummary.transactionType().equals(TransactionType.REFUND)
                        ? transactionNexusSummary.relevantAmount().negate()
                        : transactionNexusSummary.relevantAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .flatMap(amount -> Flux.fromIterable(salesTaxTracking.getTransactionNexusSummaries().values())
                        .reduce(0, ((integer, transactionNexusSummary) -> integer + (transactionNexusSummary.transactionType().equals(TransactionType.REFUND) ? 0 : 1)))
                        .map(count -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDateRange, new NexusCalculationSummary(count, amount))));

    }

    public Mono<SalesTaxTracking> addTransactionToNexusSummary(Transaction transaction, SalesTaxTracking salesTaxTracking, DateRange summaryDateRange) {
        return nexusTransactionSummaryCalculator.extract(transaction, salesTaxTracking.getNexusStateRule())
                .flatMap(transactionNexusSummary -> insertNewTransactionNexusSummary(salesTaxTracking, transaction.getComplytId(), transactionNexusSummary))
                .flatMap(transactionNexusSummary -> Mono.justOrEmpty(salesTaxTracking.getNexusCalculationSummaries().get(summaryDateRange.getEnd().toLocalDate()))
                        .defaultIfEmpty(new NexusCalculationSummary(0, BigDecimal.ZERO))
                        .map(nexusCalculationSummary -> transaction.getTransactionType().equals(TransactionType.REFUND)
                                ? nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(transactionNexusSummary.relevantAmount().negate()))
                                : nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(transactionNexusSummary.relevantAmount())).withCount(nexusCalculationSummary.count() + 1))
                        .map(nexusCalculationSummary -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDateRange, nexusCalculationSummary)))
                .defaultIfEmpty(salesTaxTracking);
    }

    public Mono<SalesTaxTracking> subtractTransactionFromNexusSummary(UUID complytId, SalesTaxTracking salesTaxTracking, DateRange summaryDateRange) {
        return Mono.justOrEmpty(salesTaxTracking.getTransactionNexusSummaries().remove(complytId))
                .flatMap(oldTransactionNexusSummary -> Mono.just(salesTaxTracking.getNexusCalculationSummaries().get(summaryDateRange.getEnd().toLocalDate()))
                        .map(nexusCalculationSummary -> oldTransactionNexusSummary.transactionType().equals(TransactionType.REFUND)
                                ? nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(oldTransactionNexusSummary.relevantAmount()))
                                : nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(oldTransactionNexusSummary.relevantAmount().negate())).withCount(nexusCalculationSummary.count() - 1)))
                .map(nexusCalculationSummary -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDateRange, nexusCalculationSummary))
                .defaultIfEmpty(salesTaxTracking);
    }

    private SalesTaxTracking insertNewNexusCalculationSummary(SalesTaxTracking salesTaxTracking, DateRange summaryDateRange, NexusCalculationSummary nexusCalculationSummary) {
        salesTaxTracking.getNexusCalculationSummaries().put(summaryDateRange.getEnd().toLocalDate(), nexusCalculationSummary);
        return salesTaxTracking;
    }

    private Mono<TransactionNexusSummary> insertNewTransactionNexusSummary(SalesTaxTracking salesTaxTracking, UUID transactionId, TransactionNexusSummary transactionNexusSummary) {
        salesTaxTracking.getTransactionNexusSummaries().put(transactionId, transactionNexusSummary);
        return Mono.just(transactionNexusSummary);
    }

    public Mono<SalesTaxTracking> calculateNexusSummaryFromTransactionSummaries(SalesTaxTracking salesTaxTracking, DateRange summaryDateRange) {
        return Flux.fromIterable(salesTaxTracking.getTransactionNexusSummaries().values())
                .filter(transactionNexusSummary -> transactionNexusSummary.externalCreatedDate().isAfter(summaryDateRange.getStart().minusNanos(1)) &&
                                                   transactionNexusSummary.externalCreatedDate().isBefore(summaryDateRange.getEnd().plusNanos(1)))
                .collectList().map(transactionNexusSummaries -> transactionNexusSummaries)
                .flatMapMany(Flux::fromIterable)
                .reduce(new NexusCalculationSummary.Builder(), (nexusCalculationSummaryBuilder, transactionNexusSummary) -> transactionNexusSummary.transactionType().equals(TransactionType.REFUND)
                        ? nexusCalculationSummaryBuilder.setAmount(nexusCalculationSummaryBuilder.getAmount().add(transactionNexusSummary.relevantAmount().negate()))
                        : nexusCalculationSummaryBuilder.setAmount(nexusCalculationSummaryBuilder.getAmount().add(transactionNexusSummary.relevantAmount())).setCount(nexusCalculationSummaryBuilder.getCount() + 1))
                .map(nexusCalculationSummaryBuilder -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDateRange, nexusCalculationSummaryBuilder.build()));
=======
        return ContextLogger.observeCtx("Calculating amount and count for all transactions on timeframe : " + nexusStateRule.getTimeFrame(), log::debug)
                .then(nexusTransactionsCountCalculator.extract(filteredTransactions, nexusStateRule)
                        .flatMap(count -> nexusTransactionsAmountCalculator.extract(filteredTransactions, nexusStateRule)
                                .flatMap(amount -> ContextLogger.observeCtx("Calculated total amount of : " + amount + ", and count : " + count, log::debug)
                                        .thenReturn(new NexusCalculationSummary(count, amount, nexusStateRule.getNexusThreshold().getDefinition())))));
>>>>>>> 91047832 (added summaryDto and mapper)
=======
    public Mono<SalesTaxTracking> calculateNexusSummary(List<Transaction> transactions, SalesTaxTracking salesTaxTracking, LocalDateTime summaryDate) {
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, salesTaxTracking.getNexusStateRule());

        return ContextLogger.observeCtx("Calculating amount and count for all transactions on timeframe : " + salesTaxTracking.getNexusStateRule().timeFrame(), log::debug)
                .then(nexusTransactionsCountCalculator.extract(filteredTransactions, salesTaxTracking.getNexusStateRule())
                        .flatMap(count -> nexusTransactionsAmountCalculator.extract(filteredTransactions, salesTaxTracking.getNexusStateRule())
                                .flatMap(amount -> ContextLogger.observeCtx("Calculated total amount of : " + amount + ", and count : " + count, log::debug)
                                        .then(insertNewNexusCalculationSummary(salesTaxTracking, summaryDate, new NexusCalculationSummary(count, amount))))));
    }

    public Mono<SalesTaxTracking> addTransactionToNexusSummary(Transaction transaction, SalesTaxTracking salesTaxTracking, LocalDateTime summaryDate) {
        return nexusTransactionsAmountCalculator.extract(List.of(transaction), salesTaxTracking.getNexusStateRule())
                .flatMap(relevantAmount -> Mono.just(salesTaxTracking.getNexusCalculationSummaries().get(summaryDate))
                        .map(nexusCalculationSummary -> transaction.getTransactionType().equals(TransactionType.REFUND)
                                ? nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(relevantAmount.negate()))
                                : nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(relevantAmount)).withCount(nexusCalculationSummary.count() + 1)))
                .flatMap(nexusCalculationSummary -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDate, nexusCalculationSummary));
    }

    public Mono<SalesTaxTracking> subtractTransactionFromNexusSummary(UUID complytId, SalesTaxTracking salesTaxTracking, LocalDateTime summaryDate) {
        return Mono.just(salesTaxTracking.getTransactionNexusSummaries().get(complytId))
                .flatMap(transactionNexusSummary -> Mono.just(salesTaxTracking.getNexusCalculationSummaries().get(summaryDate))
                        .map(nexusCalculationSummary -> transactionNexusSummary.transactionType().equals(TransactionType.REFUND)
                                ? nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(transactionNexusSummary.relevantAmount()))
                                : nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(transactionNexusSummary.relevantAmount().negate())).withCount(nexusCalculationSummary.count() - 1)))
                .flatMap(nexusCalculationSummary -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDate, nexusCalculationSummary));
    }

    private Mono<SalesTaxTracking> insertNewNexusCalculationSummary(SalesTaxTracking salesTaxTracking, LocalDateTime summaryDate, NexusCalculationSummary nexusCalculationSummary) {
        salesTaxTracking.getNexusCalculationSummaries().put(summaryDate, nexusCalculationSummary);
        return Mono.just(salesTaxTracking);
>>>>>>> 1b610118 (merged main)
    }
}