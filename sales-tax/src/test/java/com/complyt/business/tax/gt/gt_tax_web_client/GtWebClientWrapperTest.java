package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.exceptions.ComplytSalesTaxRatesException;
import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.security.TenantResolver;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ComplytGtRatesMapper;
import com.complyt.v1.models.tax.global_tax.ComplytGtRatesDto;
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
import testUtils.BaseTestClass;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GtWebClientWrapperTest extends BaseTestClass {
    @InjectMocks
    GtWebClientWrapper gtWebClientWrapper;
    @Mock
    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;
    UnitTestUtilities testUtilities;
    GtAddress gtAddress;
    LocalDateTime transactionDate;

   

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        gtAddress = testUtilities.createCanadaGtAddress();
        transactionDate = LocalDateTime.now();
    }

    @Test
    void findByAddress_ComplytSalesTaxRatesRecvieved_ComplytSalesTaxRatesReturned() {
        // Given
        ComplytGtRates complytGtRates = testUtilities.createComplytGtRates();
        ComplytGtRatesDto complytGtRatesDto = ComplytGtRatesMapper.INSTANCE.complytGtRatesToComplytGtRatesDto(complytGtRates);
        MandatoryAddress addressAsGtAddress = new MandatoryAddress(null, gtAddress.country(), null, null, null, null, gtAddress.region(), false);

        // When
        when(salesTaxRatesServiceProxy.findGtByAddress(addressAsGtAddress.country(), addressAsGtAddress.region())).thenReturn(Mono.just(complytGtRatesDto));
        Mono<ComplytGtRates> actualComplytGtRates = gtWebClientWrapper
                .findByAddress(addressAsGtAddress, transactionDate);

        // Then
        StepVerifier.create(actualComplytGtRates).expectNext(complytGtRates).verifyComplete();
    }

    @Test
    void findByAddress_RatesServiceIsUnavailable_is10RetriesExhausted() {
        // Given + When
        when(salesTaxRatesServiceProxy.findGtByAddress(gtAddress.country(), gtAddress.region()))
                .thenReturn(Mono.error(new ComplytSalesTaxRatesException()));
        Mono<ComplytGtRates> actualComplytGtRates = gtWebClientWrapper
                .findByAddress(null, gtAddress.country(), null, null, null, null, gtAddress.region(), false, transactionDate);

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
                .findByAddress(null, gtAddress.country(), null, null, null, null, gtAddress.region(), false, transactionDate);

        // Then
        StepVerifier.create(actualComplytGtRates).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void findByAddress_Returns500InternalError_ThrowsAnError() {
        // Given + When
        when(salesTaxRatesServiceProxy.findGtByAddress(gtAddress.country(), gtAddress.region()))
                .thenReturn(Mono.error(new ComplytSalesTaxRatesException()));
        Mono<ComplytGtRates> actualComplytGtRates = gtWebClientWrapper
                .findByAddress(null, gtAddress.country(), null, null, null, null, gtAddress.region(), false, transactionDate);

        // Then
        StepVerifier.create(actualComplytGtRates).expectError(ComplytSalesTaxRatesException.class).verify();
    }

}