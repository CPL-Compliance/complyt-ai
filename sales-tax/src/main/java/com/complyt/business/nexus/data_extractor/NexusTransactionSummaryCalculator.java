package com.complyt.business.nexus.data_extractor;

import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.business.transaction.CurrencyProcessor;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class NexusTransactionSummaryCalculator {

    @NonNull
    private NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    public Mono<TransactionNexusSummary> extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        return Mono.just(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction,nexusStateRule))
                .flatMap(TaxableCollectionAmountExtractor::extract)
                .map(taxableAmount -> shouldConvertToUsd(transaction) ?
                        BigDecimalProcessor.removeTrailingZeros(taxableAmount.multiply(transaction.getExchangeRateInfo().fxRate())) :
                        taxableAmount)
                .map(amount -> new TransactionNexusSummary(amount, transaction.getExternalTimestamps().getCreatedDate(), transaction.getTransactionType()));
    }

    private Boolean shouldConvertToUsd(Transaction transaction){
        return transaction.getCurrency() != null && !transaction.getCurrency().equals(CurrencyProcessor.usdCurrency);
    }
}
