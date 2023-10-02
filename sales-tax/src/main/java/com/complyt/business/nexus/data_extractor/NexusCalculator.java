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
import reactor.core.publisher.Mono;

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
    private TransactionsFilterByNexusRules transactionsFilterByNexusRules;

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
    }
}