package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
import com.complyt.utils.filter.TransactionsNexusSummariesFilterByDateRange;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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

    @NonNull
    private TransactionsNexusSummariesFilterByDateRange transactionsNexusSummariesFilterByDateRange;

    public Mono<SalesTaxTracking> addTransactionToNexusSummary(Transaction transaction, SalesTaxTracking salesTaxTracking, DateRange summaryDateRange) {
        return nexusTransactionSummaryCalculator.extract(transaction, salesTaxTracking.getNexusStateRule())
                .flatMap(transactionNexusSummary -> insertNewTransactionNexusSummary(salesTaxTracking, transaction.getComplytId(), transactionNexusSummary))
                .flatMap(transactionNexusSummary -> insertTransactionSummaryToCalculationSummary(salesTaxTracking, summaryDateRange,
                        transactionNexusSummary.relevantAmount(), transactionNexusSummary.transactionType()))
                .defaultIfEmpty(salesTaxTracking);
    }

    public Mono<SalesTaxTracking> subtractTransactionFromNexusSummary(UUID complytId, SalesTaxTracking salesTaxTracking, DateRange summaryDateRange) {
        return Mono.justOrEmpty(salesTaxTracking.getTransactionNexusSummaries().remove(complytId))
                .flatMap(transactionNexusSummary -> insertTransactionSummaryToCalculationSummary(salesTaxTracking, summaryDateRange,
                        transactionNexusSummary.relevantAmount().negate(), transactionNexusSummary.transactionType()))
                .defaultIfEmpty(salesTaxTracking);
    }

    public Mono<SalesTaxTracking> calculateTransactionNexusSummaries(@NonNull List<Transaction> transactions, @NonNull SalesTaxTracking salesTaxTracking, @NonNull DateRange summaryDateRange) {
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, salesTaxTracking.getNexusStateRule());

        return ContextLogger.observeCtx("Calculating All Transactions Summaries for timeframe : " + summaryDateRange, log::debug)
                .thenMany(Flux.fromIterable(filteredTransactions))
                .flatMap(transaction -> nexusTransactionSummaryCalculator.extract(transaction, salesTaxTracking.getNexusStateRule())
                        .flatMap(transactionNexusSummary -> insertNewTransactionNexusSummary(salesTaxTracking, transaction.getComplytId(), transactionNexusSummary)))
                .then(Mono.just(salesTaxTracking));
    }

    public Mono<SalesTaxTracking> calculateNexusSummaryFromTransactionSummaries(@NonNull SalesTaxTracking salesTaxTracking, @NonNull DateRange summaryDateRange) {
        List<TransactionNexusSummary> transactionNexusSummaries = transactionsNexusSummariesFilterByDateRange.filter(salesTaxTracking.getTransactionNexusSummaries().values().stream().toList(), summaryDateRange);

        return Flux.fromIterable(transactionNexusSummaries)
                .reduce(new NexusCalculationSummary.Builder(), (nexusCalculationSummaryBuilder, transactionNexusSummary) -> transactionNexusSummary.transactionType().equals(TransactionType.REFUND)
                        ? nexusCalculationSummaryBuilder.setAmount(nexusCalculationSummaryBuilder.getAmount().add(transactionNexusSummary.relevantAmount().negate()))
                        : nexusCalculationSummaryBuilder.setAmount(nexusCalculationSummaryBuilder.getAmount().add(transactionNexusSummary.relevantAmount())).setCount(nexusCalculationSummaryBuilder.getCount() + 1))
                .flatMap(nexusCalculationSummaryBuilder -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDateRange, nexusCalculationSummaryBuilder.build()));
    }

    private Mono<SalesTaxTracking> insertTransactionSummaryToCalculationSummary(SalesTaxTracking salesTaxTracking, DateRange summaryDateRange, BigDecimal amount, TransactionType transactionType) {
        return Mono.justOrEmpty(salesTaxTracking.getNexusCalculationSummaries().get(summaryDateRange.getEnd().toLocalDate()))
                .defaultIfEmpty(new NexusCalculationSummary(0, BigDecimal.ZERO))
                .map(nexusCalculationSummary -> transactionType.equals(TransactionType.REFUND)
                        ? nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(amount.negate()))
                        : nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(amount)).withCount(nexusCalculationSummary.count() + (amount.floatValue() > 0 ? 1 : -1)))
                .flatMap(nexusCalculationSummary -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDateRange, nexusCalculationSummary));
    }

    private Mono<SalesTaxTracking> insertNewNexusCalculationSummary(SalesTaxTracking salesTaxTracking, DateRange summaryDateRange, NexusCalculationSummary nexusCalculationSummary) {
        return Mono.just(salesTaxTracking.getNexusCalculationSummaries())
                .map(map -> {
                    map.put(summaryDateRange.getEnd().toLocalDate(), nexusCalculationSummary);
                    return salesTaxTracking.withNexusCalculationSummaries(map);
                });
    }

    private Mono<TransactionNexusSummary> insertNewTransactionNexusSummary(SalesTaxTracking salesTaxTracking, UUID transactionId, TransactionNexusSummary transactionNexusSummary) {
        return Mono.just(salesTaxTracking.getTransactionNexusSummaries())
                .map(map -> {
                    map.put(transactionId, transactionNexusSummary);
                    return transactionNexusSummary;
                });
    }
}