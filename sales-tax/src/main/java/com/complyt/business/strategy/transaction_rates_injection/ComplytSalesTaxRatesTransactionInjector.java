package com.complyt.business.strategy.transaction_rates_injection;

import com.complyt.business.builder.CollectionBuilder;
//import com.complyt.business.tax.sales_tax.mapper.ComplytSalesTaxRatesToSalesTaxRates;
import com.complyt.business.tax.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.tax.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.business.transaction.data_injector.TransactionCityCountyInjector;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.CityCountyWrapper;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class ComplytSalesTaxRatesTransactionInjector implements RatesTransactionInjector<ComplytSalesTaxRates> {

    @NonNull
    private TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @NonNull
    private SalesTaxAggregator salesTaxAggregator;

    @NonNull
    private TransactionCityCountyInjector transactionCityCountyInjector;


    @Override
    public Function<ComplytSalesTaxRates, Mono<Transaction>> inject(Transaction transaction) {
        return complytSalesTaxRates -> setTransactionSalesTaxRates(transaction, complytSalesTaxRates)
                .flatMap(transactionWithRates -> injectCityCountyData(transactionWithRates, complytSalesTaxRates))
                .map(transactionWithRatesAndCounty -> calculateFinalTransactionAmounts(transactionWithRatesAndCounty, complytSalesTaxRates));
    }

    private Mono<Transaction> setTransactionSalesTaxRates(Transaction transaction, ComplytSalesTaxRates complytSalesTaxRates) {
        SalesTaxRates salesTaxRates = complytSalesTaxRates.salesTaxRates();
        return transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates);
    }

    private Mono<Transaction> injectCityCountyData(Transaction transaction, ComplytSalesTaxRates complytSalesTaxRates) {
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(
                complytSalesTaxRates.address().city(),
                complytSalesTaxRates.address().county()
        );
        return transactionCityCountyInjector.inject(cityCountyWrapper, transaction);
    }

    private Transaction calculateFinalTransactionAmounts(Transaction transaction, ComplytSalesTaxRates complytSalesTaxRates) {
        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);
        BigDecimal salesTaxAmount = salesTaxAggregator.aggregate(taxables, transaction.getIsTaxInclusive());
        SalesTaxRates salesTaxRates = complytSalesTaxRates.salesTaxRates();
        UUID complytId = complytSalesTaxRates.complytId();

        SalesTax salesTax = new SalesTax(complytId, salesTaxAmount, salesTaxRates.taxRate(), salesTaxRates, null);
        BigDecimal finalAmount = transaction.getIsTaxInclusive() ?
                transaction.getFinalTransactionAmount() :
                transaction.getFinalTransactionAmount().add(salesTaxAmount);

        return transaction.setSalesTax(salesTax).setFinalTransactionAmount(finalAmount);
    }
}
