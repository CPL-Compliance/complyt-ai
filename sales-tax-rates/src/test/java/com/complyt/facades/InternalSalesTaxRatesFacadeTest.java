package com.complyt.facades;

import com.complyt.domain.*;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.facade.InternalSalesTaxRatesFacade;
import com.complyt.services.AddressValidationService;
import com.complyt.services.ExternalSalesTaxRatesServiceImpl;
import com.complyt.services.InternalSalesTaxRatesServiceImpl;
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
    InternalSalesTaxRatesServiceImpl internalSalesTaxRatesService;

    @Mock
    ExternalSalesTaxRatesServiceImpl<ComplytSalesTaxRates> externalSalesTaxRatesService;

    private InternalSalesTaxRatesFacade internalSalesTaxRatesFacade;
    private AddressWithDate addressWithDate;
    private CommonSalesTaxRates commonSalesTaxRates;
    private Address address;
    private InternalSalesTaxRates internalSalesTaxRates;
    private SalesTaxRatesData salesTaxRatesData;

    @BeforeEach
    void setUp() {
        internalSalesTaxRatesFacade = new InternalSalesTaxRatesFacade(addressValidationService, internalSalesTaxRatesService, externalSalesTaxRatesService);
        addressWithDate = TestUtilities.createAddressInCaliforniaWithCreationDate();
        commonSalesTaxRates = TestUtilities.createExternalCommonSalesTaxRates();
        address = TestUtilities.createAddressInCalifornia();
        internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now());
        salesTaxRatesData = TestUtilities.createSalesTaxRatesData();
    }
    @Test
    void findByAddress_RatesReturnedFromInternalService_ReturnsRates() {
        // When
        when(addressValidationService.validate(addressWithDate.getAddress())).thenReturn(Mono.just(salesTaxRatesData.matchedAddressData()));
        when(internalSalesTaxRatesService.findByAddress(addressWithDate)).thenReturn(Mono.just(commonSalesTaxRates));
        salesTaxRatesData = salesTaxRatesData.withComplytId(commonSalesTaxRates.complytId()).withRequestAddress(addressWithDate);

        Mono<SalesTaxRatesData> commonSalesTaxRatesMono = internalSalesTaxRatesFacade.validateAddress(addressWithDate);

        // Then
        StepVerifier.create(commonSalesTaxRatesMono)
                .expectNext(salesTaxRatesData)
                .verifyComplete();
    }

    @Test
    void findByAddress_RatesReturnedFromExternalService_ReturnsRates() {
        // When
        when(internalSalesTaxRatesService.findByAddress(addressWithDate)).thenReturn(Mono.empty());
        when(externalSalesTaxRatesService.findByAddress(addressWithDate)).thenReturn(Mono.just(commonSalesTaxRates));
        salesTaxRatesData = salesTaxRatesData.withComplytId(commonSalesTaxRates.complytId()).withRequestAddress(addressWithDate);

        Mono<CommonSalesTaxRates> commonSalesTaxRatesMono = internalSalesTaxRatesFacade.findByAddress(addressWithDate);

        // Then
        StepVerifier.create(commonSalesTaxRatesMono)
                .expectNext(commonSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void validate_NullAddressPassed_ThrowsException() {
        // Given
        AddressWithDate nullAddressWithDate = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> internalSalesTaxRatesFacade.validateAddress(nullAddressWithDate));

        assertEquals("addressWithDate is marked non-null but is null", nullPointerException.getMessage());
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
        when(internalSalesTaxRatesService.save(internalSalesTaxRates)).thenReturn(Mono.just(internalSalesTaxRates));

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