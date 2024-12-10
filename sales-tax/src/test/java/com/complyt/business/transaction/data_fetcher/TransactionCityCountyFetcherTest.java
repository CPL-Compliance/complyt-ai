package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyWrapper;
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
class TransactionCityCountyFetcherTest {

    @InjectMocks
    private TransactionCityCountyFetcher transactionCityCountyFetcher;
    @Mock
    AddressValidationWebClientWrapper<Address> addressAddressValidationWebClientWrapper;

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
        Address address = transaction.getShippingAddress();


        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(address.city(), address.county());

        // When
        when(addressAddressValidationWebClientWrapper.validateAddress(address)).thenReturn(Mono.just(address));
        Mono<CityCountyWrapper> cityCountyWrapperMono = transactionCityCountyFetcher.fetch(address);

        // Then
        StepVerifier.create(cityCountyWrapperMono).expectNext(cityCountyWrapper).verifyComplete();
    }

}
