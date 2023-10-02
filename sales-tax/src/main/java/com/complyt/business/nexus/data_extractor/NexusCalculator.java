package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class NexusCalculator {

    @NonNull
    private NexusTransactionsAmountCalculator nexusTransactionsAmountCalculator;

    @NonNull
    private NexusTransactionsCountCalculator nexusTransactionsCountCalculator;

    @NonNull
    private NexusTransactionSummaryCalculator nexusTransactionSummaryCalculator;

    @NonNull
    private TransactionsFilterByNexusRules transactionsFilterByNexusRules;

    public Mono<SalesTaxTracking> calculateNexusSummary(List<Transaction> transactions, SalesTaxTracking salesTaxTracking, LocalDateTime summaryDate) {
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, salesTaxTracking.getNexusStateRule());

        return ContextLogger.observeCtx("Calculating amount and count for all transactions on timeframe : " + salesTaxTracking.getNexusStateRule().timeFrame(), log::debug)
                .thenMany(Flux.fromIterable(filteredTransactions)).flatMap(transaction -> nexusTransactionSummaryCalculator.extract(transaction, salesTaxTracking.getNexusStateRule())
                        .mapNotNull(transactionNexusSummary -> salesTaxTracking.getTransactionNexusSummaries().put(transaction.getComplytId(), transactionNexusSummary)))
                .map(transactionNexusSummary -> transactionNexusSummary.transactionType().equals(TransactionType.REFUND)
                        ? transactionNexusSummary.relevantAmount().negate()
                        : transactionNexusSummary.relevantAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .flatMap(amount -> Flux.fromIterable(salesTaxTracking.getTransactionNexusSummaries().values())
                        .reduce(0, ((integer, transactionNexusSummary) -> integer + (transactionNexusSummary.transactionType().equals(TransactionType.REFUND) ? 0 : 1)))
                        .mapNotNull(count -> salesTaxTracking.getNexusCalculationSummaries().put(summaryDate, new NexusCalculationSummary(count, amount))))
                .thenReturn(salesTaxTracking);

    }

    public Mono<SalesTaxTracking> addTransactionToNexusSummary(Transaction transaction, SalesTaxTracking salesTaxTracking, LocalDateTime summaryDate) {
        return nexusTransactionSummaryCalculator.extract(transaction, salesTaxTracking.getNexusStateRule())
                .mapNotNull(transactionNexusSummary -> salesTaxTracking.getTransactionNexusSummaries().put(transaction.getComplytId(), transactionNexusSummary))
                .flatMap(transactionNexusSummary -> Mono.just(salesTaxTracking.getNexusCalculationSummaries().get(summaryDate))
                        .map(nexusCalculationSummary -> transaction.getTransactionType().equals(TransactionType.REFUND)
                                ? nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(transactionNexusSummary.relevantAmount().negate()))
                                : nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(transactionNexusSummary.relevantAmount())).withCount(nexusCalculationSummary.count() + 1))
                        .flatMap(nexusCalculationSummary -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDate, nexusCalculationSummary)))
                .switchIfEmpty(Mono.just(salesTaxTracking));
    }

    public Mono<SalesTaxTracking> subtractTransactionFromNexusSummary(UUID complytId, SalesTaxTracking salesTaxTracking, LocalDateTime summaryDate) {
        return Mono.just(salesTaxTracking.getTransactionNexusSummaries().remove(complytId))
                .flatMap(transactionNexusSummary -> Mono.just(salesTaxTracking.getNexusCalculationSummaries().get(summaryDate))
                        .map(nexusCalculationSummary -> transactionNexusSummary.transactionType().equals(TransactionType.REFUND)
                                ? nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(transactionNexusSummary.relevantAmount()))
                                : nexusCalculationSummary.withAmount(nexusCalculationSummary.amount().add(transactionNexusSummary.relevantAmount().negate())).withCount(nexusCalculationSummary.count() - 1)))
                .flatMap(nexusCalculationSummary -> insertNewNexusCalculationSummary(salesTaxTracking, summaryDate, nexusCalculationSummary));
    }

    private Mono<SalesTaxTracking> insertNewNexusCalculationSummary(SalesTaxTracking salesTaxTracking, LocalDateTime summaryDate, NexusCalculationSummary nexusCalculationSummary) {
        salesTaxTracking.getNexusCalculationSummaries().put(summaryDate, nexusCalculationSummary);
        return Mono.just(salesTaxTracking);
    }
}