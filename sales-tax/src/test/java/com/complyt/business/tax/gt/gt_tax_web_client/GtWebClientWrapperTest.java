package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.exceptions.ComplytSalesTaxRatesException;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ComplytGtRatesMapper;
import com.complyt.v1.models.sales_tax.gt.ComplytGtRatesDto;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.lang.module.FindException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GtWebClientWrapperTest {
    @InjectMocks
    GtWebClientWrapper gtWebClientWrapper;
    @Mock
    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;
    UnitTestUtilities testUtilities;
    GtAddress gtAddress;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        gtAddress = testUtilities.createCanadaGtAddress();
    }

    @Test
    void findByAddress_ComplytSalesTaxRatesRecvieved_ComplytSalesTaxRatesReturned() {
        // Given
        ComplytGtRates complytGtRates = testUtilities.createComplytGtRates();
        ComplytGtRatesDto complytGtRatesDto = ComplytGtRatesMapper.INSTANCE.complytGtRatesToComplytGtRatesDto(complytGtRates);
        Address addressAsGtAddress = new Address(null, gtAddress.country(), null, null, null, null, gtAddress.region(), false);

        // When
        when(salesTaxRatesServiceProxy.findGtByAddress(addressAsGtAddress.country(), addressAsGtAddress.region())).thenReturn(Mono.just(complytGtRatesDto));
        Mono<ComplytGtRates> actualComplytGtRates = gtWebClientWrapper
                .findByAddress(addressAsGtAddress);

        // Then
        StepVerifier.create(actualComplytGtRates).expectNext(complytGtRates).verifyComplete();
    }

    @Test
    void findByAddress_RatesServiceIsUnavailable_is10RetriesExhausted() {
        // Given + When
        when(salesTaxRatesServiceProxy.findGtByAddress(gtAddress.country(), gtAddress.region()))
                .thenReturn(Mono.error(new ComplytSalesTaxRatesException()));
        Mono<ComplytGtRates> actualComplytGtRates = gtWebClientWrapper
                .findByAddress(null, gtAddress.country(), null, null, null, null, gtAddress.region(), false);

        // Then
        StepVerifier.create(actualComplytGtRates)
                .expectErrorMatches(
                        throwable -> throwable instanceof ComplytSalesTaxRatesException
                                && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void findByAddress_invalidAddress_ReturnsObjectNotFoundApiException() {
        // Given + When
        when(salesTaxRatesServiceProxy.findGtByAddress(gtAddress.country(), gtAddress.region()))
                .thenReturn(Mono.error(testUtilities.create404NodFoundFeignException()));

        Mono<ComplytGtRates> actualComplytGtRates = gtWebClientWrapper
                .findByAddress(null, gtAddress.country(), null, null, null, null, gtAddress.region(), false);

        // Then
        StepVerifier.create(actualComplytGtRates).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void findByAddress_Returns500InternalError_ThrowsAnError() {
        // Given + When
        when(salesTaxRatesServiceProxy.findGtByAddress(gtAddress.country(), gtAddress.region()))
                .thenReturn(Mono.error(new ComplytSalesTaxRatesException()));
        Mono<ComplytGtRates> actualComplytGtRates = gtWebClientWrapper
                .findByAddress(null, gtAddress.country(), null, null, null, null, gtAddress.region(), false);

        // Then
        StepVerifier.create(actualComplytGtRates).expectError(ComplytSalesTaxRatesException.class).verify();
    }

}