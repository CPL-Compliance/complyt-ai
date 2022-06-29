package com.complyt.facades;

import com.complyt.business.transaction.TransactionProductClassificationInjector;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.services.TransactionService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Component
@AllArgsConstructor
@Slf4j
public class TransactionFacade {
    @Qualifier("transactionServiceImpl")
    @NonNull
    private TransactionService transactionService;

    @Qualifier("salesTaxServiceImpl")
    @NonNull
    private SalesTaxService salesTaxService;

    @Qualifier("productClassificationServiceImpl")
    @NonNull
    private ProductClassificationService productClassificationService;

    public Mono<Transaction> save(Transaction transaction) {
        return transactionService.save(transaction);
    }

    public Mono<Transaction> upsert(@NonNull String externalId, Transaction transaction) {
        return transactionService.upsert(externalId, transaction);
    }

    public Mono<Transaction> update(@NonNull String externalId, Transaction transaction) {
        return transactionService.update(externalId, transaction);
    }

    public Mono<Transaction> findByExternalId(String externalId) {
        return transactionService.findByExternalId(externalId);
    }

    public Flux<Transaction> getAll() {
        return transactionService.findAll();
    }

    public Mono<Transaction> updateSalesTax(String externalId) {
        return transactionService
                .findByExternalId(externalId)
                .map(TransactionProductClassificationInjector::new)
                .flatMap(injectRulesToTransactionItems())
                .flatMap(setSalesTaxToTransaction())
                .flatMap(transaction -> transactionService.update(externalId, transaction));
    }

    private Function<TransactionProductClassificationInjector, Mono<Transaction>> injectRulesToTransactionItems() {
        return transactionProductClassificationInjector -> Flux.fromIterable(transactionProductClassificationInjector.getTransaction().getItems())
                .flatMap(item -> getClassification(item.getTaxCode()))
                .collectMap(productClassification -> productClassification.getTaxCode(), productClassification -> productClassification)
                .flatMap(transactionProductClassificationInjector::act);
    }

    private Function<Transaction, Mono<Transaction>> setSalesTaxToTransaction() {
        return transaction -> salesTaxService.findByAddress(transaction.getShippingAddress())
                .map(salesTaxData -> salesTaxService.salesTaxDataToSalesTaxRate(salesTaxData))
                .map(injectSalesTaxToTransaction(transaction));
    }
    
    private Function<SalesTaxRate, Transaction> injectSalesTaxToTransaction(Transaction transaction) {
        return salesTaxRate -> {
            log.info("Setting sales tax rates for transaction's items");
            List<Item> itemsWithRates = salesTaxService.setSalesTaxRatesForItems(transaction.getItems(), salesTaxRate);
            Transaction transactionWithItemsWithRates = transaction.withItems(itemsWithRates);
            log.info("Calculating total sales tax amount for transaction");
            float salesTaxAmount = salesTaxService.calculateSalesTaxAmount(transactionWithItemsWithRates.getItems());
            SalesTax salesTax = new SalesTax(salesTaxAmount, salesTaxRate);
            log.debug("transaction's sales tax : " + salesTax);
            return transactionWithItemsWithRates.withSalesTax(salesTax);
        };
    }

    public Mono<ProductClassification> getClassification(String taxCode) {
        log.debug("Searching for product classification for tax code : " + taxCode);
        return productClassificationService.findOneByTaxCode(taxCode);
    }

    public Mono<Transaction> markAsCancelled(String transactionId) {
        return transactionService.markAsCancelled(transactionId);
    }
}