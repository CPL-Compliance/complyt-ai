package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxApplyCheck;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.utils.data_injector.TransactionSalesTaxInjector;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    private SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRate;

    @NonNull
    private SalesTaxApplyCheck salesTaxApplyCheck;

    @NonNull
    private TransactionSalesTaxInjector transactionSalesTaxInjector;

    @Override
    public Mono<Transaction> handleSalesTaxCalculation(@NonNull Transaction transactionWithOutSalesTax, @NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxApplyCheck.isApplied(transactionWithOutSalesTax, salesTaxTracking)
                .flatMap(isApplied -> isApplied ? calculate(transactionWithOutSalesTax) : Mono.just(transactionWithOutSalesTax));
    }

    @Override
    public Mono<Transaction> calculate(@NonNull Transaction transaction) {
        return salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())
                .map(salesTaxDataToSalesTaxRate::map)
                .flatMap(salesTaxRate -> transactionSalesTaxInjector.inject(new Pair<>(transaction, salesTaxRate)));
    }
}
