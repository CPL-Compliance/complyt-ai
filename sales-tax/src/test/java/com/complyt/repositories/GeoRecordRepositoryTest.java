package com.complyt.repositories;

import com.complyt.domain.transaction.GeoRecord;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class GeoRecordRepositoryTest {

    @InjectMocks
    GeoRecordRepository geoRecordRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Transaction transaction;

    UnitTestUtilities testUtilities;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void findStateByZip_FindsZipCodeAndStateObject_ReturnsZipCodeAndStateObject() {
        // Given
        String zipCode = transaction.getShippingAddress().zip();
        Query query = Query.query(Criteria.where("zip").is(zipCode));
        GeoRecord geoRecord = new GeoRecord("1", "Zip", "CA");

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        when(reactiveMongoTemplate.findOne(query, GeoRecord.class)).thenReturn(Mono.just(geoRecord));

        Mono<GeoRecord> zipCodeAndStateObjectMono = geoRecordRepository.findStateByZip(transaction.getShippingAddress().zip());

        // Then
        StepVerifier.create(zipCodeAndStateObjectMono).expectNext(geoRecord).verifyComplete();
    }

    @Test
    void findStateByZip_ZipCodeIsNull_ThrowsNullPointerException() {
        // Given
        String zipCode = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> geoRecordRepository.findStateByZip(zipCode));

        // Then
        assertEquals(nullPointerException.getMessage(), "zipCode is marked non-null but is null");
    }
}