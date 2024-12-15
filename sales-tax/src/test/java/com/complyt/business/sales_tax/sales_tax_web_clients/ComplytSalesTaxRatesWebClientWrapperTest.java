package com.complyt.business.sales_tax.sales_tax_web_clients;

import org.junit.jupiter.api.Test;
import org.junit.runner.Request;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import com.complyt.business.exceptions.ComplytSalesTaxRatesException;
import com.complyt.business.exceptions.FeignErrorUtils;
import com.complyt.business.tax.sales_tax.models.ComplytInternalSalesTaxRatesDto;
import com.complyt.business.tax.sales_tax.sales_tax_web_clients.ComplytSalesTaxRatesClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
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

    String requiredDate;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        transactionCreatedDateTime = LocalDateTime.now();
        requiredDate = transactionCreatedDateTime.toString();
    }

    @Test
    void findByAddress_validAddress_ReturnsSalesTaxData() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();
        ComplytInternalSalesTaxRatesDto complytInternalSalesTaxRatesDto = UnitTestUtilities.createComplytInternalSalesTaxRatesDto();
        ComplytSalesTaxRates expextedComplytSalesTaxRates = ComplytSalesTaxRatesMapper.INSTANCE.complytSalesTaxRatesDtoToComplytSalesTaxRates(complytInternalSalesTaxRatesDto);

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(),
                address.city(), address.street(), address.zip(),
                address.isPartial(),transactionCreatedDateTime.toString())).thenReturn(Mono.just(complytInternalSalesTaxRatesDto));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(expextedComplytSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_invalidAddress_ReturnsObjectNotFoundApiException() {
        // Given
        Address address = testUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial(), requiredDate))
                .thenReturn(Mono.error(testUtilities.create404NodFoundFeignException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void findByAddress_Returns404InternalError_ReturnsObjectNotFoundApiException() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial(), requiredDate))
                .thenReturn(Mono.error(testUtilities.create404NodFoundFeignException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void findByAddress_RatesServiceIsUnavailable_is5RetriesExhausted() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(),
                address.city(), address.street(), address.zip(),
                address.isPartial(),transactionCreatedDateTime.toString())).thenReturn(Mono.error(new ComplytSalesTaxRatesException()));

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
        Address address = UnitTestUtilities.createAddressInCalifornia();
        FeignException.NotFound notFoundException = testUtilities.create404NodFoundFeignException();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(),
                address.street(), address.zip(), address.isPartial(), requiredDate))
                .thenReturn(Mono.error(notFoundException));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono)
                .expectError(ObjectNotFoundApiException.class)
                .verify();

        verify(salesTaxRatesServiceProxy, times(1)).findByAddress(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());
    }

    @Test
    void findByAddress_BadRequestException_ThrowsObjectNotValidApiException() {
        // Given
        Address address = UnitTestUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesServiceProxy.findByAddress(address.state(), address.country(), address.county(), address.city(),
                address.street(), address.zip(), address.isPartial(), requiredDate))
                .thenReturn(Mono.error(UnitTestUtilities.create400BadRequestFeignException()));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesClientWrapper.findByAddress(address, transactionCreatedDateTime);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof ObjectNotValidApiException
                )
                .verify();

        verify(salesTaxRatesServiceProxy, times(1)).findByAddress(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());
    }
}