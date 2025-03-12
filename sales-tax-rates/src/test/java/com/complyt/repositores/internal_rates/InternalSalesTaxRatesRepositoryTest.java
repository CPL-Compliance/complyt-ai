package com.complyt.repositores.internal_rates;

import com.complyt.business.internal_sales_tax_rates.InternalRatesCollectionNames;
import com.complyt.domain.Address;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.repositories.internal_rates.InternalRatesAddressQueryBuilder;
import com.complyt.repositories.internal_rates.InternalSalesTaxRatesRepository;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)

@ExtendWith(MockitoExtension.class)
class InternalSalesTaxRatesRepositoryTest {
    @Mock
    TenantResolver tenantResolver;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @InjectMocks
    private InternalSalesTaxRatesRepository repository;

    @Mock
    InternalRatesAddressQueryBuilder internalRatesAddressQueryBuilder;


    private Address address;
    private AddressWithDate addressWithDate;
    private InternalSalesTaxRates internalSalesTaxRates;
    private Query query;
    private String collectionName;

    @BeforeEach
    void setUp() {
        address = TestUtilities.createAddressInCalifornia();
        addressWithDate = TestUtilities.createAddressInCaliforniaWithCreationDate();
        internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now());
        query = new Query();
        collectionName = InternalRatesCollectionNames.stateInternalCollectionName(address.state());
    }

    @Test
    void find_NullAddressWithDate_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> repository.find(null));
    }

    @Test
    void save_NullInternalSalesTaxRates_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }


    @Test
    void testFind_WithValidAddressWithDate() {
        // Arrange
        when(tenantResolver.resolve()).thenReturn(Mono.just("12345"));
        when(internalRatesAddressQueryBuilder.build(addressWithDate.getAddress())).thenReturn(query);
        when(reactiveMongoTemplate.findOne(query, InternalSalesTaxRates.class, collectionName))
                .thenReturn(Mono.just(internalSalesTaxRates));

        // Act
        Mono<InternalSalesTaxRates> result = repository.find(addressWithDate);

        // Assert
        StepVerifier.create(result)
                .expectNext(internalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void testFind_NoMatchingInternalSalesTaxRates_ShouldReturnEmptyMono() {
        // Arrange
        when(internalRatesAddressQueryBuilder.build(addressWithDate.getAddress())).thenReturn(query);
        when(reactiveMongoTemplate.findOne(query, InternalSalesTaxRates.class, collectionName))
                .thenReturn(Mono.empty());
        when(tenantResolver.resolve()).thenReturn(Mono.just("12345"));

        // Act
        Mono<InternalSalesTaxRates> result = repository.find(addressWithDate);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    // Functional test for save method

    @Test
    void testSave_WithValidInternalSalesTaxRates() {
        when(reactiveMongoTemplate.save(internalSalesTaxRates, collectionName)).thenReturn(Mono.just(internalSalesTaxRates));

        // Act
        Mono<InternalSalesTaxRates> result = repository.save(internalSalesTaxRates);

        // Assert
        StepVerifier.create(result)
                .expectNext(internalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void archive_validRate_callsFindAndRemoveThenSave() {
        // Given
        when(internalRatesAddressQueryBuilder.build(internalSalesTaxRates)).thenReturn(query);
        when(reactiveMongoTemplate.findAndRemove(query, InternalSalesTaxRates.class, collectionName))
                .thenReturn(Mono.just(internalSalesTaxRates));

        when(reactiveMongoTemplate.save(internalSalesTaxRates, InternalRatesCollectionNames.ARCHIVED_COLLECTION_NAME)).thenReturn(Mono.just(internalSalesTaxRates));

        // When
        Mono<InternalSalesTaxRates> result = repository.archive(internalSalesTaxRates);

        // Then
        StepVerifier.create(result)
                .expectNext(internalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void archive_noRateFound_returnsEmptyMono() {
        // Given
        when(internalRatesAddressQueryBuilder.build(internalSalesTaxRates)).thenReturn(query);
        when(reactiveMongoTemplate.findAndRemove(query, InternalSalesTaxRates.class, collectionName))
                .thenReturn(Mono.empty());

        // When
        Mono<InternalSalesTaxRates> result = repository.archive(internalSalesTaxRates);

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // Should complete without emitting any value
    }

    @Test
    void updateRate_validRate_callsFindAndReplaceThenSave() {
        // Given
        when(internalRatesAddressQueryBuilder.build(internalSalesTaxRates)).thenReturn(query);
        when(reactiveMongoTemplate.findOne(query, InternalSalesTaxRates.class, collectionName)).thenReturn(Mono.just(internalSalesTaxRates));
        when(reactiveMongoTemplate.findAndReplace(eq(query), eq(internalSalesTaxRates), any(FindAndReplaceOptions.class), eq(collectionName)))
                .thenReturn(Mono.just(internalSalesTaxRates));
        when(reactiveMongoTemplate.save(internalSalesTaxRates, InternalRatesCollectionNames.ARCHIVED_COLLECTION_NAME)).thenReturn(Mono.just(internalSalesTaxRates));

        // When
        Mono<InternalSalesTaxRates> result = repository.updateRate(internalSalesTaxRates);

        // Then
        StepVerifier.create(result)
                .expectNext(internalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void updateRate_noRateFound_returnsEmptyMono() {
        // Given
        when(internalRatesAddressQueryBuilder.build(internalSalesTaxRates)).thenReturn(query);
        when(reactiveMongoTemplate.findOne(query, InternalSalesTaxRates.class, collectionName)).thenReturn(Mono.empty());

        // When
        Mono<InternalSalesTaxRates> result = repository.updateRate(internalSalesTaxRates);

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // Should complete without emitting any value
    }

    @Test
    void archive_nullInternalSalesTaxRates_throwsException() {
        assertThrows(NullPointerException.class, () -> repository.archive(null));
    }

    @Test
    void updateRate_nullInternalSalesTaxRates_throwsException() {
        assertThrows(NullPointerException.class, () -> repository.updateRate(null));
    }
}