package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.business.exceptions.ComplytSalesTaxRatesException;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.ComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import com.complyt.v1.models.sales_tax.ComplytSalesTaxRatesDto;
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
public class ComplytSalesTaxRatesWebClientWrapperTest {
    @InjectMocks
    ComplytSalesTaxRatesClientWrapper complytSalesTaxRatesClientWrapper;

    @Mock
    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
    }

    @Test
    void findByAddress_validAddress_ReturnsSalesTaxData() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        ComplytSalesTaxRatesDto complytSalesTaxRatesDto = ComplytSalesTaxRatesMapper.INSTANCE.complytSalesTaxRatesToComplytSalesTaxRatesDto(complytSalesTaxRates);

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial())).thenReturn(Mono.just(complytSalesTaxRatesDto));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(complytSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_invalidAddress_ReturnsObjectNotFoundApiException() {
        // Given
        Address address = testUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(testUtilities.create404NodFoundFeignException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void findByAddress_Returns404InternalError_ReturnsObjectNotFoundApiException() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(testUtilities.create404NodFoundFeignException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void findByAddress_RatesServiceIsUnavailable_is5RetriesExhausted() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(new ComplytSalesTaxRatesException()));
        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof ComplytSalesTaxRatesException
                                && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

}