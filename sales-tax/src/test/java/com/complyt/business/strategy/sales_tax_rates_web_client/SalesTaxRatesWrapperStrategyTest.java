package com.complyt.business.strategy.sales_tax_rates_web_client;

import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.business.tax.gt.gt_tax_web_client.GtWebClientWrapper;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.ComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesTaxRatesWrapperStrategyTest {

    @InjectMocks
    SalesTaxRatesWrapperStrategy salesTaxRatesWrapperStrategy;
    @Mock
    ComplytSalesTaxRatesClientWrapper salesTaxWebClientWrapper;
    @Mock
    GtWebClientWrapper gtWebClientWrapper;
    Transaction transaction;
    UnitTestUtilities testUtilities;


    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        salesTaxRatesWrapperStrategy = new SalesTaxRatesWrapperStrategy(salesTaxWebClientWrapper, gtWebClientWrapper);
    }

    @Test
    void select_TransactionAddressCountryIsUsa_RunsUsaFunction() {
        // Given
        ComplytSalesTaxRates expectedComplytSalesTaxRates = testUtilities.createCaliforniaComplytSalesTaxRates();

        // When
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress()))
                .thenReturn(Mono.just(testUtilities.createCaliforniaComplytSalesTaxRates()));
        // Then
        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = (Mono<ComplytSalesTaxRates>) salesTaxRatesWrapperStrategy.select(transaction).apply(transaction.getShippingAddress());
        StepVerifier.create(complytSalesTaxRatesMono).equals(expectedComplytSalesTaxRates);
    }

    @Test
    void select_TransactionAddressCountryIsNotUsa_RunsNonUsaFunction() {
        // Given
        ComplytGtRates expectedComplytGtRates = testUtilities.createComplytGtRates();
        Transaction givenTransaction = transaction.withShippingAddress(transaction.getShippingAddress().withCountry("Canada"));

        // When
        when(gtWebClientWrapper.findByAddress(givenTransaction.getShippingAddress()))
                .thenReturn(Mono.just(testUtilities.createComplytGtRates()));
        // Then
        Mono<ComplytGtRates> complytSalesTaxRatesMono = (Mono<ComplytGtRates>) salesTaxRatesWrapperStrategy.select(givenTransaction).apply(givenTransaction.getShippingAddress());
        StepVerifier.create(complytSalesTaxRatesMono).equals(expectedComplytGtRates);
    }
}