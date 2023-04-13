package com.example.complyt.repositores;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.repositories.SalesTaxRatesRepository;
import com.testUtils.TestUtilities;
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
    SalesTaxRatesRepository salesTaxRatesRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByAddress_FindsAddressWithSalesTaxRates_ReturnsAddressWithSalesTaxRates() {
        // Given
        AddressWithSalesTaxRates addressWithSalesTaxRates = TestUtilities.createCaliforniaAddressWithSalesTaxRates();
        Address address = TestUtilities.createAddressInCalifornia();
        Query query = TestUtilities.createAddressSearchQuery(address);
        String state = "new_york";

        // When
        when(reactiveMongoTemplate.findOne(query, AddressWithSalesTaxRates.class, state)).thenReturn(Mono.just(addressWithSalesTaxRates));
        Mono<AddressWithSalesTaxRates> addressWithSalesTaxRatesMono = salesTaxRatesRepository.findByAddress(address, state);

        // Then
        StepVerifier.create(addressWithSalesTaxRatesMono).expectNext(addressWithSalesTaxRates).verifyComplete();
    }

    @Test
    void save_SavesAddressWithSalesTaxRates_ReturnsAddressWithSalesTaxRates() {
        // Given
        AddressWithSalesTaxRates addressWithSalesTaxRates = TestUtilities.createCaliforniaAddressWithSalesTaxRates();
        String state = "california";

        // When
        when(reactiveMongoTemplate.save(addressWithSalesTaxRates, state)).thenReturn(Mono.just(addressWithSalesTaxRates));
        Mono<AddressWithSalesTaxRates> addressWithSalesTaxRatesMono = salesTaxRatesRepository.save(addressWithSalesTaxRates, state);

        // Then
        StepVerifier.create(addressWithSalesTaxRatesMono).expectNext(addressWithSalesTaxRates).verifyComplete();
    }

    @Test
    void save_NullCollectionPassed_ThrowsException() {
        // Given
        AddressWithSalesTaxRates addressWithSalesTaxRates = TestUtilities.createCaliforniaAddressWithSalesTaxRates();
        String nullCollection = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxRatesRepository.save(addressWithSalesTaxRates, nullCollection);
        });

        assertEquals(nullPointerException.getMessage(), "collection is marked non-null but is null");
    }

    @Test
    void save_NullAddressWithSalesTaxRatesPassed_ThrowsException() {
        // Given
        AddressWithSalesTaxRates nullAddressWithSalesTaxRates = null;
        String state = "california";


        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxRatesRepository.save(nullAddressWithSalesTaxRates, state);
        });

        assertEquals(nullPointerException.getMessage(), "addressWithSalesTaxRates is marked non-null but is null");
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
