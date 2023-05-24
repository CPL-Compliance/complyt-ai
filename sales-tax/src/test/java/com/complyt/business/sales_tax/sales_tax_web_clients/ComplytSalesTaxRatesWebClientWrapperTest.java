package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import com.complyt.v1.models.ComplytSalesTaxRatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComplytSalesTaxRatesWebClientWrapperTest {
    @InjectMocks
    ComplytSalesTaxRatesClientWrapper complytSalesTaxRatesClientWrapper;

    @Mock
    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    @BeforeEach
    void setUp() {

    }

    @Test
    void findByAddress_validAddress_ReturnsSalesTaxData() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();
        ComplytSalesTaxRates complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        ComplytSalesTaxRatesDto complytSalesTaxRatesDto = ComplytSalesTaxRatesMapper.INSTANCE.complytSalesTaxRatesToComplytSalesTaxRatesDto(complytSalesTaxRates);

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip())).thenReturn(Mono.just(complytSalesTaxRatesDto));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(complytSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_Returns500InternalError_ThrowsAnError() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip())).thenReturn(Mono.error(new ObjectNotFoundApiException()));
        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectError(ObjectNotFoundApiException.class).verify();
    }

}