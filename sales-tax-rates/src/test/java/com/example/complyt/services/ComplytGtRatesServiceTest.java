package com.example.complyt.services;

import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.domain.gt.GtAddress;
import com.complyt.repositories.gt_rates.ComplytGtRatesRepository;
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
public class ComplytGtRatesServiceTest {

    @InjectMocks
    ComplytGtRatesService complytGtRatesService;

    @Mock
    ComplytGtRatesRepository complytGtRatesRepository;
    GtAddress gtAddress;
    ComplytGtRates complytGtRates;

    @BeforeEach
    void setUp() {
        gtAddress = TestUtilities.createCanadaGtAddress();
        complytGtRates = TestUtilities.createCanadaComplytGtRates();
    }

    @Test
    void findByAddress_FindsComplytGtRates_ReturnsComplytGtRates() {
        // Given + When
        when(complytGtRatesRepository.findByAddress(gtAddress)).thenReturn(Mono.just(complytGtRates));
        Mono<ComplytGtRates> complytGtRatesMono = complytGtRatesService.findByAddress(gtAddress);

        // Then
        StepVerifier.create(complytGtRatesMono).expectNext(complytGtRates).verifyComplete();
    }

    @Test
    void findByAddress_NullGtAddressPassed_ThrowsNullPointerException() {
        // Given
        GtAddress nullGtAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            complytGtRatesService.findByAddress(nullGtAddress);
        });

        assertEquals(NullPointerException.class, nullPointerException.getClass());
    }

}
