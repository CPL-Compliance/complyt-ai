package com.complyt.services;

import com.complyt.business.strategy.StrategySelector;
import com.complyt.business.tax.sales_tax.checker.SalesTaxApplyCheck;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    @Qualifier("exemptionServiceImpl")
    private ExemptionService exemptionService;

    @NonNull
    private StrategySelector salesTaxRatesWrapperStrategy;

    @NonNull
    private StrategySelector transactionRatesInjectionStrategy;

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
        return ((Mono<ComplytInternalRates>) salesTaxRatesWrapperStrategy.select(transaction).apply(transaction.getShippingAddress()))
                .flatMap(complytInternalRates -> (Mono<Transaction>)transactionRatesInjectionStrategy.select(transaction).apply(complytInternalRates));
    }
}