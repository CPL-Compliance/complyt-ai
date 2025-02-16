package com.complyt.facades;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.SalesTaxRatesData;
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
        SalesTaxRatesData salesTaxRatesData = TestUtilities.createSalesTaxRatesData().withComplytId(expectedCommonSalesTaxRatesFacade.complytId()).withRequestAddress(addressWithTransactionDate);


                // When
        when(complytSalesTaxRatesService.findByAddress(addressWithTransactionDate)).thenReturn(Mono.just(expectedCommonSalesTaxRatesFacade));
        when(addressValidationService.validate(addressWithTransactionDate.getAddress())).thenReturn(Mono.just(salesTaxRatesData.matchedAddressData()));

        Mono<SalesTaxRatesData> complytSalesTaxRatesMono = complytSalesTaxRatesFacade.validateAddress(addressWithTransactionDate, false);
        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(salesTaxRatesData).verifyComplete();
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        AddressWithDate nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> complytSalesTaxRatesFacade.validateAddress(nullAddress, false));

        assertEquals(nullPointerException.getMessage(), "requestAddressWithDate " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }
}
