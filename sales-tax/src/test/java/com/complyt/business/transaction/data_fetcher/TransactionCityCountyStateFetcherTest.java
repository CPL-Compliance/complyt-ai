package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.StubComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyStateWrapper;
import com.complyt.domain.transaction.Transaction;
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
class TransactionCityCountyStateFetcherTest {

    @InjectMocks
    private TransactionCityCountyStateFetcher transactionCityCountyStateFetcher;
    @Mock
    StubComplytSalesTaxRatesClientWrapper salesTaxWebClientWrapper;
    UnitTestUtilities testUtilities;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void inject_InjectsCounty_ReturnsTransaction() {
        // Given
        Address addressWithCounty = UnitTestUtilities.createAddressInCalifornia().withCounty("Fresno");
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates()
                .withAddress(addressWithCounty);


        CityCountyStateWrapper cityCountyStateWrapper = new CityCountyStateWrapper(complytSalesTaxRates.address().city(), addressWithCounty.county(), complytSalesTaxRates.address().state());

        // When
        when(salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())).thenReturn(Mono.just(complytSalesTaxRates));
        Mono<CityCountyStateWrapper> cityCountyStateWrapperMono = transactionCityCountyStateFetcher.fetch(transaction.getShippingAddress());

        // Then
        StepVerifier.create(cityCountyStateWrapperMono).expectNext(cityCountyStateWrapper).verifyComplete();
    }

}
