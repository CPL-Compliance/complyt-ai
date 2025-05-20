package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.business.exceptions.ComplytSalesTaxRatesException;
import com.complyt.business.tax.sales_tax.models.ComplytInternalSalesTaxRatesDto;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.ComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.security.TenantResolver;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import feign.FeignException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplytSalesTaxRatesWebClientWrapperTest {
    @InjectMocks
    ComplytSalesTaxRatesClientWrapper complytSalesTaxRatesClientWrapper;

    @Mock
    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    UnitTestUtilities testUtilities;

    LocalDateTime transactionCreatedDateTime;

    String effectiveDate;



    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        transactionCreatedDateTime = LocalDateTime.now();
        effectiveDate = transactionCreatedDateTime.toString();
    }

    @Test
    void findByAddress_validAddress_ReturnsSalesTaxData() {
        // Given
        ShippingAddress address = UnitTestUtilities.createShippingAddressInCalifornia();
        ComplytInternalSalesTaxRatesDto complytInternalSalesTaxRatesDto = UnitTestUtilities.createComplytInternalSalesTaxRatesDto();
        ComplytSalesTaxRates expextedComplytSalesTaxRates = ComplytSalesTaxRatesMapper.INSTANCE.complytSalesTaxRatesDtoToComplytSalesTaxRates(complytInternalSalesTaxRatesDto);

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(),
                address.city(), address.street(), address.zip(),
                address.isPartial(),transactionCreatedDateTime.toString(), true)).thenReturn(Mono.just(complytInternalSalesTaxRatesDto));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(expextedComplytSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_invalidAddress_ReturnsObjectNotFoundApiException() {
        // Given
        ShippingAddress address = UnitTestUtilities.createShippingAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial(), effectiveDate, true))
                .thenReturn(Mono.error(testUtilities.create404NodFoundFeignException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void findByAddress_Returns404InternalError_ReturnsObjectNotFoundApiException() {
        // Given
        ShippingAddress address = UnitTestUtilities.createShippingAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial(), effectiveDate, true))
                .thenReturn(Mono.error(testUtilities.create404NodFoundFeignException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void findByAddress_RatesServiceIsUnavailable_is5RetriesExhausted() {
        // Given
        ShippingAddress address = UnitTestUtilities.createShippingAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(),
                address.city(), address.street(), address.zip(),
                address.isPartial(),transactionCreatedDateTime.toString(), true)).thenReturn(Mono.error(new ComplytSalesTaxRatesException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof ComplytSalesTaxRatesException
                                && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void findByAddress_NotFoundException_NoRetryOccurs() {
        // Given
        ShippingAddress address = UnitTestUtilities.createShippingAddressInCalifornia();
        FeignException.NotFound notFoundException = testUtilities.create404NodFoundFeignException();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(),
                address.street(), address.zip(), address.isPartial(), effectiveDate, true))
                .thenReturn(Mono.error(notFoundException));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono)
                .expectError(ObjectNotFoundApiException.class)
                .verify();

        verify(salesTaxRatesServiceProxy, times(1)).findByAddress(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString(), anyBoolean());
    }

    @Test
    void findByAddress_BadRequestException_ThrowsObjectNotValidApiException() {
        // Given
        ShippingAddress address = UnitTestUtilities.createShippingAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(),
                address.street(), address.zip(), address.isPartial(), effectiveDate, true))
                .thenReturn(Mono.error(UnitTestUtilities.create400BadRequestFeignException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof ObjectNotValidApiException
                )
                .verify();

        verify(salesTaxRatesServiceProxy, times(1)).findByAddress(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString(), anyBoolean());
    }
}