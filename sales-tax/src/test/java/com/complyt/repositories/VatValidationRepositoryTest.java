package com.complyt.repositories;

import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.domain.transaction.GeoRecord;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class VatValidationRepositoryTest {

    @InjectMocks
    VatValidationRepository vatValidationRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    UnitTestUtilities testUtilities;

    VatDetailsToValidate vatDetailsToValidate;

    ValidatedVat validatedVat;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        vatDetailsToValidate = testUtilities.createVatDetailsToValidate();
        validatedVat = testUtilities.createValidatedVat();
    }

    @Test
    void find_FindsZipCodeAndStateObject_ReturnsZipCodeAndStateObject() {
        // Given
        Query query = Query.query(Criteria.where("countryCode").is(vatDetailsToValidate.getCountryCode())
                .and("vatNumber").is(vatDetailsToValidate.getVatNumber()));

        // When
        when(reactiveMongoTemplate.findOne(query, ValidatedVat.class)).thenReturn(Mono.just(validatedVat));

        Mono<ValidatedVat> resultValidatedVat = vatValidationRepository.find(vatDetailsToValidate);

        // Then
        StepVerifier.create(resultValidatedVat).expectNext(validatedVat).verifyComplete();
    }

    @Test
    void find_VatDetailsIsNull_ThrowsNullPointerException() {
        // Given
        VatDetailsToValidate details = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> vatValidationRepository.find(details));

        // Then
        assertEquals(nullPointerException.getMessage(), "vatDetails is marked non-null but is null");
    }

    @Test
    void save_ValidatedVat_ReturnValidatedVat() {
        // When
        when(reactiveMongoTemplate.save(validatedVat)).thenReturn(Mono.just(validatedVat));

        // Then
        Mono<ValidatedVat> savedValidatedVat = vatValidationRepository.save(validatedVat);

        StepVerifier.create(savedValidatedVat).expectNext(validatedVat).verifyComplete();
    }

    @Test
    void save_ValidatedVatIsNull_ThrowsNullPointerException() {
        // Given
        ValidatedVat NullValidatedVat = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> vatValidationRepository.save(NullValidatedVat));

        // Then
        assertEquals(nullPointerException.getMessage(), "validatedVat is marked non-null but is null");
    }
}