package com.example.complyt.facades;

import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.domain.gt.GtAddress;
import com.complyt.facade.ComplytGtRatesFacade;
import com.complyt.services.ComplytGtRatesService;
import org.junit.jupiter.api.BeforeEach;
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
public class ComplytGtRatesFacadeTest {

    @InjectMocks
    ComplytGtRatesFacade complytGtRatesFacade;

    @Mock
    ComplytGtRatesService complytGtRatesService;

    ComplytGtRates complytGtRates;
    GtAddress gtAddress;

    @BeforeEach
    void setUp() {
        gtAddress = TestUtilities.createCanadaGtAddress();
        complytGtRates = TestUtilities.createCanadaComplytGtRates();
    }

    @Test
    void findByAddress_RatesReturnedFromService_ReturnsRates() {

        // Given + When
        when(complytGtRatesService.findByAddress(gtAddress)).thenReturn(Mono.just(complytGtRates));
        Mono<ComplytGtRates> actualComplytGtRates = complytGtRatesFacade.findByAddress(gtAddress);

        // Then
        StepVerifier.create(actualComplytGtRates).expectNext(complytGtRates).verifyComplete();
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        GtAddress nullGtAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> complytGtRatesFacade.findByAddress(nullGtAddress));

        assertEquals(nullPointerException.getMessage(), "gtAddress " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

}