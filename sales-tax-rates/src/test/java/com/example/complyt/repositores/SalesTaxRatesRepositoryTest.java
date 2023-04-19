package com.example.complyt.repositores;

import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.repositories.ComplytSalesTaxRatesRepository;
import testUtils.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SalesTaxRatesRepositoryTest {

    @InjectMocks
    ComplytSalesTaxRatesRepository salesTaxRatesRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByAddress_FindsComplytSalesTaxRates_ReturnsComplytSalesTaxRates() {
        // Given
        ComplytSalesTaxRates complytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        Address address = TestUtilities.createAddressInCalifornia();
        Query query = TestUtilities.createAddressSearchQuery(address);
        String state = "new_york";

        // When
        when(reactiveMongoTemplate.findOne(query, ComplytSalesTaxRates.class, state)).thenReturn(Mono.just(complytSalesTaxRates));
        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = salesTaxRatesRepository.findByAddress(address, state);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(complytSalesTaxRates).verifyComplete();
    }

    @Test
    void save_SavesComplytSalesTaxRates_ReturnsComplytSalesTaxRates() {
        // Given
        ComplytSalesTaxRates complytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        String state = "california";

        // When
        when(reactiveMongoTemplate.save(complytSalesTaxRates, state)).thenReturn(Mono.just(complytSalesTaxRates));
        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = salesTaxRatesRepository.save(complytSalesTaxRates, state);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(complytSalesTaxRates).verifyComplete();
    }

    @Test
    void save_NullCollectionPassed_ThrowsException() {
        // Given
        ComplytSalesTaxRates complytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        String nullCollection = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxRatesRepository.save(complytSalesTaxRates, nullCollection);
        });

        assertEquals(nullPointerException.getMessage(), "collection is marked non-null but is null");
    }

    @Test
    void save_NullComplytSalesTaxRatesPassed_ThrowsException() {
        // Given
        ComplytSalesTaxRates nullComplytSalesTaxRates = null;
        String state = "california";

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxRatesRepository.save(nullComplytSalesTaxRates, state);
        });

        assertEquals(nullPointerException.getMessage(), "complytSalesTaxRates is marked non-null but is null");
    }

    @Test
    void findByAddress_NullCollectionPassed_ThrowsException() {
        // Given
        Address address = TestUtilities.createAddressInCalifornia();
        String nullCollection = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxRatesRepository.findByAddress(address, nullCollection);
        });

        assertEquals(nullPointerException.getMessage(), "collection is marked non-null but is null");
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        Address nullAddress = null;
        String state = "california";

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxRatesRepository.findByAddress(nullAddress, state);
        });

        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }
}
