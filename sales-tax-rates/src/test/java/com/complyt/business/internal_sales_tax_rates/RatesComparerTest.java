//package com.complyt.business.internal_sales_tax_rates;
//
//import com.complyt.domain.internal_rates.InternalRates;
//import com.complyt.domain.internal_rates.InternalSalesTaxRates;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import testUtils.TestUtilities;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThrows;
//
//@ExtendWith(MockitoExtension.class)
//class RatesComparerTest {
//    InternalSalesTaxRates internalSalesTaxRates;
//    InternalRates internalRates;
//    RatesComparer ratesComparer;
//
//    @BeforeEach
//    void setUp() {
//        ratesComparer = new RatesComparer();
//        internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now(), UUID.randomUUID());
//        internalRates = internalSalesTaxRates.getRates();
//    }
//
//
//    @Test
//    void compareInternalSalesTaxRates_WithEqualRates_ReturnsTrue() {
//        Mono<Boolean> result = ratesComparer.compareInternalSalesTaxRates(internalSalesTaxRates, internalSalesTaxRates);
//
//        StepVerifier.create(result)
//                .expectNext(true)
//                .verifyComplete();
//    }
//
//    @Test
//    void compareInternalSalesTaxRates_WithDifferentRates_ReturnsFalse() {
//        InternalSalesTaxRates anotherInternalSalesTaxRates = internalSalesTaxRates.withRates(internalRates.withCityRate(BigDecimal.valueOf(0.1)));
//        Mono<Boolean> result = ratesComparer.compareInternalSalesTaxRates(internalSalesTaxRates, anotherInternalSalesTaxRates);
//
//        StepVerifier.create(result)
//                .expectNext(false)
//                .verifyComplete();
//    }
//
//    @Test
//    void compareInternalSalesTaxRates_WithDifferentDates_ReturnsFalse() {
//        InternalSalesTaxRates anotherInternalSalesTaxRates = internalSalesTaxRates.withRates(internalRates.withEffectiveDate(internalRates.getEffectiveDate().plusDays(1)));
//        Mono<Boolean> result = ratesComparer.compareInternalSalesTaxRates(internalSalesTaxRates, anotherInternalSalesTaxRates);
//
//        StepVerifier.create(result)
//                .expectNext(false)
//                .verifyComplete();
//    }
//
//    @Test
//    void compareInternalSalesTaxRates_WithDifferentAddress_ReturnsFalse() {
//        InternalSalesTaxRates anotherInternalSalesTaxRates = internalSalesTaxRates.withAddress(internalSalesTaxRates.getAddress().withCity("anotherCity"));
//        Mono<Boolean> result = ratesComparer.compareInternalSalesTaxRates(internalSalesTaxRates, anotherInternalSalesTaxRates);
//
//        StepVerifier.create(result)
//                .expectNext(false)
//                .verifyComplete();
//    }
//
//
//    @Test
//    void areInternalRatesDifferOnlyByDate_WithDifferentDates_ReturnsTrue() {
//        InternalRates newInternalRates = internalRates.withEffectiveDate(internalRates.getEffectiveDate().plusDays(1));
//
//        boolean result = ratesComparer.areInternalRatesDifferOnlyByDate(internalRates, newInternalRates);
//
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    void areInternalRatesDifferOnlyByDate_WithSameDates_ReturnsFalse() {
//        InternalRates anotherInternalRates = internalRates.withEffectiveDate(internalRates.getEffectiveDate());
//
//        boolean result = ratesComparer.areInternalRatesDifferOnlyByDate(internalRates, anotherInternalRates);
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    void areInternalRatesDifferOnlyByDate_WithSameDatesDifferentRates_ReturnsFalse() {
//        InternalRates anotherInternalRates = internalRates.withEffectiveDate(internalRates.getEffectiveDate());
//        anotherInternalRates = anotherInternalRates.withCityRate(BigDecimal.valueOf(0.1));
//
//        boolean result = ratesComparer.areInternalRatesDifferOnlyByDate(internalRates, anotherInternalRates);
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    void areInternalRatesDifferOnlyByRate_WithDifferentRates_ReturnsTrue() {
//        InternalRates newInternalRates = internalRates.withStateRate(BigDecimal.ONE);
//
//        boolean result = ratesComparer.areInternalRatesDifferOnlyByRate(internalRates, newInternalRates);
//
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    void areInternalRatesDifferOnlyByRate_WithSameRates_ReturnsFalse() {
//        InternalRates anotherInternalRates = internalRates.withCityRate(internalRates.getCityRate());
//
//        boolean result = ratesComparer.areInternalRatesDifferOnlyByRate(internalRates, anotherInternalRates);
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    void areInternalRateEqual_WithEqualRates_ReturnsTrue() {
//        boolean result = ratesComparer.areInternalRateEqual(internalRates, internalRates);
//
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    void areInternalRateEqual_WithDifferentRates_ReturnsFalse() {
//        InternalRates anotherInternalRates = internalRates.withCityRate(BigDecimal.valueOf(0.1));
//        boolean result = ratesComparer.areInternalRateEqual(internalRates, anotherInternalRates);
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    void areInternalRateEqual_WithEqualRatesDifferentDates_ReturnsFalse() {
//        InternalRates anotherInternalRates = internalRates.withEffectiveDate(internalRates.getEffectiveDate().plusDays(1));
//        boolean result = ratesComparer.areInternalRateEqual(internalRates, anotherInternalRates);
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    void compareInternalRatesByRatesValues_WithEqualValues_ReturnsTrue() {
//        boolean result = ratesComparer.compareInternalRatesByRatesValues(internalRates, internalRates);
//
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    void compareInternalRatesByRatesValues_WithDifferentValues_ReturnsFalse() {
//        InternalRates newInternalRates = internalRates.withStateRate(BigDecimal.ONE);
//
//        boolean result = ratesComparer.compareInternalRatesByRatesValues(internalRates, newInternalRates);
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    void compareRatesByRatesDates_WithEqualDates_ReturnsTrue() {
//        boolean result = ratesComparer.areRatesDatesEquals(internalRates, internalRates);
//
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    void compareRatesByRatesDates_WithDifferentDates_ReturnsFalse() {
//        InternalRates newInternalRates = internalRates.withEffectiveDate(internalRates.getEffectiveDate().plusDays(1));
//
//        boolean result = ratesComparer.areRatesDatesEquals(internalRates, newInternalRates);
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    void compareInternalSalesTaxRates_NullNewRate_ThrowsException() {
//        // Given
//        InternalSalesTaxRates nullNewRate = null;
//
//        // When + Then
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            ratesComparer.compareInternalSalesTaxRates(nullNewRate, internalSalesTaxRates);
//        });
//
//        assertEquals("existingRate is marked non-null but is null", nullPointerException.getMessage());
//    }
//
//    @Test
//    void compareInternalSalesTaxRates_Null_ThrowsException() {
//        // When + Then
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            ratesComparer.compareInternalSalesTaxRates(internalSalesTaxRates, null);
//        });
//
//        assertEquals("newRate is marked non-null but is null", nullPointerException.getMessage());
//    }
//
//    @Test
//    void compareInternalRatesByRatesValues_CountyRateZero_ReturnsTrue() {
//        boolean result = ratesComparer.compareInternalRatesByRatesValues(internalRates.withCountyRate(BigDecimal.ZERO), internalRates.withCountyRate(BigDecimal.ZERO));
//
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    void compareInternalRatesByRatesValues_DifferentCountyRates_ReturnsFalse() {
//        boolean result = ratesComparer
//                .compareInternalRatesByRatesValues(internalRates, internalRates.withCountyRate(BigDecimal.ZERO));
//
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    void compareInternalRatesByRatesValues_RatesDatesEquals_ReturnsTrue() {
//        boolean result = ratesComparer.areRatesDatesEquals(internalRates, internalRates);
//
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    void compareInternalRatesByRatesValues_InternalRatesDifferOnlyByDate_ReturnsTrue() {
//        InternalRates diffByDate = internalRates.withEffectiveDate(LocalDateTime.now().plusDays(1));
//        boolean result = ratesComparer.areInternalRatesDifferOnlyByDate(diffByDate, internalRates);
//
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    void compareInternalRatesByRatesValues_compareInternalSalesTaxRates_ReturnsTrue() {
//        boolean result = Boolean.TRUE.equals(ratesComparer.compareInternalSalesTaxRates(internalSalesTaxRates, internalSalesTaxRates).block());
//
//        assertThat(result).isTrue();
//    }
//
//}