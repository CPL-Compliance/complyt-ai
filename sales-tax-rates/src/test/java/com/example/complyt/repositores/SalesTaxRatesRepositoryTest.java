package com.example.complyt.repositores;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.repositories.SalesTaxRatesRepository;
import com.testUtils.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
        AddressWithSalesTaxRates addressWithSalesTaxRates = TestUtilities.createNewYorkAddressWithSalesTaxRates();
        Address address = TestUtilities.createAddressInNewYork();
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
        AddressWithSalesTaxRates addressWithSalesTaxRates = TestUtilities.createNewYorkAddressWithSalesTaxRates();
        String state = "new_york";

        // When
        when(reactiveMongoTemplate.save(addressWithSalesTaxRates, state)).thenReturn(Mono.just(addressWithSalesTaxRates));
        Mono<AddressWithSalesTaxRates> addressWithSalesTaxRatesMono = salesTaxRatesRepository.save(addressWithSalesTaxRates, state);

        // Then
        StepVerifier.create(addressWithSalesTaxRatesMono).expectNext(addressWithSalesTaxRates).verifyComplete();
    }
}
