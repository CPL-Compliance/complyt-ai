package com.complyt.services;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.strategy.StrategySelector;
import com.complyt.business.tax.sales_tax.checker.SalesTaxApplyCheck;
import com.complyt.business.transaction.RefundTransactionProcessor;
import com.complyt.business.transaction.items_amounts.AmountCalculator;
import com.complyt.domain.Taxable;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.ComplytInternalRates;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

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

    @NonNull
    private RefundTransactionProcessor refundTransactionProcessor;

    @NonNull
    private AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @Override
    public Mono<Transaction> handleSalesTaxCalculation(@NonNull Transaction transactionWithOutSalesTax, @NonNull SalesTaxTracking salesTaxTracking, @NonNull Customer customer) {
        if (refundTransactionProcessor.isLinkedRefundFromAnInvoice(transactionWithOutSalesTax)) {
            return refundTransactionProcessor.setInvoiceSalesTaxToLinkedRefund(transactionWithOutSalesTax)
                    .flatMap(this::calculateTransactionLevelTaxRate);
        }

        SalesTaxApplyCheck salesTaxApplyCheck = new SalesTaxApplyCheck(transactionWithOutSalesTax);
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTracking);

        return isApplied ? exemptionService.isFullyExempted(transactionWithOutSalesTax)
                .flatMap(isFullyExempted -> calculate(transactionWithOutSalesTax, isFullyExempted || customer.getCustomerType() == CustomerType.MARKETPLACE)
                        .flatMap(this::calculateTransactionLevelTaxRate)) :
                Mono.just(transactionWithOutSalesTax);
    }

    @Override
    public Mono<Transaction> calculate(@NonNull Transaction transaction, @NonNull Boolean isExempt) {
        return ((Mono<ComplytInternalRates>) salesTaxRatesWrapperStrategy.select(transaction).apply(transaction.getShippingAddress()))
                .flatMap(complytInternalRates -> (Mono<Transaction>) transactionRatesInjectionStrategy.select(transaction).apply(Pair.with(complytInternalRates, isExempt)));
    }

    public Mono<Transaction> calculateTransactionLevelTaxRate(@NonNull Transaction transaction) {
        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);
        BigDecimal totalItemsAmount = totalItemsAmountCalculator.calculate(taxables, transaction.getIsTaxInclusive());

        return Objects.equals(transaction.getTotalItemsAmount(), BigDecimal.ZERO) ?
                Mono.just(transaction) :
                Mono.just(transaction.setSalesTax(transaction.getSalesTax()
                        .withRate(transaction.getSalesTax().amount().divide(totalItemsAmount, 4, RoundingMode.HALF_UP))));
    }

}