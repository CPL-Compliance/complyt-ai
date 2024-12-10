package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.TaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.facade.InternalSalesTaxRatesFacade;
import com.complyt.services.AddressValidationService;
import com.complyt.services.SalesTaxRatesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalSalesTaxRatesFacadeTest {
    @Mock
    private AddressValidationService addressValidationService;

    @Mock
    private SalesTaxRatesService<TaxRates> salesTaxRatesService;

    @InjectMocks
    private InternalSalesTaxRatesFacade internalSalesTaxRatesFacade;

    private AddressWithDate addressWithDate;
    private CommonSalesTaxRates commonSalesTaxRates;
    private Address address;
    private InternalSalesTaxRates internalSalesTaxRates;

    @BeforeEach
    void setUp() {
        addressWithDate = TestUtilities.createAddressInCaliforniaWithCreationDate();
        commonSalesTaxRates = TestUtilities.createExternalCommonSalesTaxRates();
        address = TestUtilities.createAddressInCalifornia();
        internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now());
    }
    @Test
    void findByAddress_RatesReturnedFromInternalService_ReturnsRates() {
        // When
        when(addressValidationService.validate(addressWithDate.getAddress())).thenReturn(Mono.just(address));
        when(salesTaxRatesService.findByAddress(addressWithDate)).thenReturn(Mono.just(commonSalesTaxRates));

        Mono<CommonSalesTaxRates> commonSalesTaxRatesMono = internalSalesTaxRatesFacade.findByAddress(addressWithDate);

        // Then
        StepVerifier.create(commonSalesTaxRatesMono)
                .expectNext(commonSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void findByAddress_RatesNotFoundInInternalService_FallbackToExternalService() {
        // When
        when(addressValidationService.validate(addressWithDate.getAddress())).thenReturn(Mono.empty());
        when(salesTaxRatesService.findByAddress(addressWithDate)).thenReturn(Mono.just(commonSalesTaxRates));

        Mono<CommonSalesTaxRates> commonSalesTaxRatesMono = internalSalesTaxRatesFacade.findByAddress(addressWithDate);

        // Then
        StepVerifier.create(commonSalesTaxRatesMono)
                .expectNext(commonSalesTaxRates)
                .verifyComplete();
    }


    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        AddressWithDate nullAddressWithDate = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> internalSalesTaxRatesFacade.findByAddress(nullAddressWithDate));

        assertEquals("addressWithDate is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void save_SuccessfulSave_ReturnsSavedRates() {
        // When
        when(salesTaxRatesService.save(internalSalesTaxRates)).thenReturn(Mono.just(internalSalesTaxRates));

        Mono<InternalSalesTaxRates> internalSalesTaxRatesMono = internalSalesTaxRatesFacade.save(internalSalesTaxRates);

        // Then
        StepVerifier.create(internalSalesTaxRatesMono)
                .expectNext(internalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void save_NullRatesPassed_ThrowsException() {
        // Given
        InternalSalesTaxRates nullTaxRates = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> internalSalesTaxRatesFacade.save(nullTaxRates));

        assertEquals("internalSalesTaxRates is marked non-null but is null", nullPointerException.getMessage());
    }
}