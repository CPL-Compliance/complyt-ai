package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxRatesController;
import com.complyt.business.sales_tax.checker.SalesTaxApplyCheck;
import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    private SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRate;

    @NonNull
    @Qualifier("exemptionServiceImpl")
    private ExemptionService exemptionService;

    @NonNull
    private SalesTaxCalculator salesTaxCalculator;

    @NonNull
    private SalesTaxRatesController salesTaxRatesController;

    @Override
    public Mono<Transaction> handleSalesTaxCalculation(@NonNull Transaction transactionWithOutSalesTax, @NonNull SalesTaxTracking salesTaxTracking) {
        SalesTaxApplyCheck salesTaxApplyCheck = new SalesTaxApplyCheck(transactionWithOutSalesTax);
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTracking);

        return isApplied ? exemptionService.isFullyExempted(transactionWithOutSalesTax)
                .flatMap(isFullyExempted -> isFullyExempted ? Mono.just(transactionWithOutSalesTax) : calculate(transactionWithOutSalesTax)) :
                Mono.just(transactionWithOutSalesTax);
    }

    @Override
    public Mono<Transaction> calculate(@NonNull Transaction transaction) {
        return salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())
                .map(salesTaxDataToSalesTaxRate::map)
                .map(injectSalesTaxToTransaction(transaction));
    }

    private Function<SalesTaxRate, Transaction> injectSalesTaxToTransaction(Transaction transaction) {
        return salesTaxRate -> {
            log.info("Setting sales tax rates for transaction's items");
            Transaction transactionWithRates = salesTaxRatesController.setRates(transaction, salesTaxRate);

            log.info("Calculating total sales tax amount for transaction");
            float salesTaxAmount = salesTaxCalculator.calculate(transactionWithRates.getItems());
            SalesTax salesTax = new SalesTax(salesTaxAmount, salesTaxRate);

            log.debug("Transaction's sales tax : " + salesTax);
            return transactionWithRates.withSalesTax(salesTax);
        };
    }

}