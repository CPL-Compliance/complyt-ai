package com.complyt.services;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.sales_tax.checker.SalesTaxApplyCheck;
import com.complyt.business.sales_tax.mapper.ComplytSalesTaxRatesToSalesTaxRates;
import com.complyt.business.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.sales_tax.sales_tax_rates.TransactionSalesTaxRatesHandler;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTax;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    private SalesTaxWebClientWrapper<ComplytSalesTaxRates> salesTaxWebClientWrapper;

    @NonNull
    private ComplytSalesTaxRatesToSalesTaxRates complytSalesTaxRatesToSalesTaxRates;

    @NonNull
    @Qualifier("exemptionServiceImpl")
    private ExemptionService exemptionService;

    @NonNull
    private SalesTaxAggregator salesTaxAggregator;

    @NonNull
    private TransactionSalesTaxRatesHandler transactionSalesTaxRatesHandler;

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @Override
    public Mono<Transaction> handleSalesTaxCalculation(@NonNull Transaction transactionWithOutSalesTax, @NonNull SalesTaxTracking salesTaxTracking, @NonNull Customer customer) {
        SalesTaxApplyCheck salesTaxApplyCheck = new SalesTaxApplyCheck(transactionWithOutSalesTax);
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTracking);

        return isApplied ? exemptionService.isFullyExempted(transactionWithOutSalesTax)
                .flatMap(isFullyExempted -> isFullyExempted || customer.getCustomerType() == CustomerType.MARKETPLACE ? Mono.just(transactionWithOutSalesTax) :
                        calculate(transactionWithOutSalesTax)) :
                Mono.just(transactionWithOutSalesTax);
    }

    @Override
    public Mono<Transaction> calculate(@NonNull Transaction transaction) {
        return salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())
                .flatMap(createFunctionInjectSalesTaxToTransaction(transaction));
    }

    private Function<ComplytSalesTaxRates, Mono<Transaction>> createFunctionInjectSalesTaxToTransaction(Transaction transaction) {
        return complytSalesTaxRates -> complytSalesTaxRatesToSalesTaxRates.map(complytSalesTaxRates)
                .flatMap(salesTaxRates -> transactionSalesTaxRatesHandler.setRates(transaction, salesTaxRates)
                        .map(transactionWithRates -> {
                            List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transactionWithRates);
                            float salesTaxAmount = salesTaxAggregator.aggregate(taxables);
                            SalesTax salesTax = new SalesTax(salesTaxAmount, salesTaxRates);

                            return transactionWithRates.withSalesTax(salesTax);
                        }));
    }

    private boolean isCustomerTypeMarketplace(Transaction transaction) {
        return transaction.getCustomer().getCustomerType() == CustomerType.MARKETPLACE;
    }
}