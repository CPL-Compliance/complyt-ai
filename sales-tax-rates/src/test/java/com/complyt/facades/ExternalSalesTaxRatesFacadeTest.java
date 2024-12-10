package com.complyt.facades;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.facade.ExternalSalesTaxRatesFacade;
import com.complyt.services.AddressValidationService;
import com.complyt.services.SalesTaxRatesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExternalSalesTaxRatesFacadeTest {

    @InjectMocks
    ExternalSalesTaxRatesFacade complytSalesTaxRatesFacade;

    @Mock
    SalesTaxRatesService<ComplytSalesTaxRates> complytSalesTaxRatesService;

    @Mock
    AddressValidationService addressValidationService;

    @Test
    void findByAddress_RatesReturnedFromService_ReturnsRates() {
        // Given
        CommonSalesTaxRates expectedCommonSalesTaxRatesFacade = TestUtilities.createExternalCommonSalesTaxRates();
        AddressWithDate addressWithTransactionDate = TestUtilities.createAddressInCaliforniaWithCreationDate();


        // When
        when(complytSalesTaxRatesService.findByAddress(addressWithTransactionDate)).thenReturn(Mono.just(expectedCommonSalesTaxRatesFacade));
        when(addressValidationService.validate(addressWithTransactionDate.getAddress())).thenReturn(Mono.just(addressWithTransactionDate.getAddress()));

        Mono<CommonSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesFacade.findByAddress(addressWithTransactionDate);
        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(expectedCommonSalesTaxRatesFacade).verifyComplete();
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        AddressWithDate nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> complytSalesTaxRatesFacade.findByAddress(nullAddress));

        assertEquals(nullPointerException.getMessage(), "addressWithDate " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }
}
