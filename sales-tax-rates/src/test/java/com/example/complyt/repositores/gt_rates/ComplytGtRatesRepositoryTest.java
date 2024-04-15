package com.example.complyt.repositores.gt_rates;

import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.domain.gt.GtAddress;
import com.complyt.domain.gt.GtRates;
import com.complyt.repositories.QueryBuilder;
import com.complyt.repositories.gt_rates.ComplytGtRatesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComplytGtRatesRepositoryTest {

    @InjectMocks
    ComplytGtRatesRepository complytGtRatesRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    QueryBuilder<GtAddress> gtCountriesQueryBuilder;

    GtAddress gtAddress;
    ComplytGtRates complytGtRates;

    @BeforeEach
    void setUp() {
        gtAddress = new GtAddress("Canada", "Quebec");
        GtRates gtRates = new GtRates(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975));
        complytGtRates = new ComplytGtRates(UUID.randomUUID().toString(), gtAddress, gtRates);
    }

    @Test
    void findByAddress_FindsComplytGtRates_ReturnsComplytGtRates() {
        // Given
        GtAddress gtAddress = new GtAddress("Canada", "Quebec");
        Query query = Query.query(Criteria.where("gtAddress.country").regex(gtAddress.country(), "i"));

        Optional.ofNullable(gtAddress.region()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            query.addCriteria(Criteria.where("gtAddress.region").regex(escapedSearchString, "i"));
        });

        // When
        when(gtCountriesQueryBuilder.build(gtAddress)).thenReturn(query);
        when(reactiveMongoTemplate.findOne(query, ComplytGtRates.class)).thenReturn(Mono.just(complytGtRates));
        Mono<ComplytGtRates> complytGtRatesMono = complytGtRatesRepository.findByAddress(gtAddress);

        // Then
        StepVerifier.create(complytGtRatesMono).expectNext(complytGtRates).verifyComplete();
    }

    @Test
    void findByAddress_NullGtAddressPassed_ThrowsException() {
        // Given
        GtAddress nullGtAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            complytGtRatesRepository.findByAddress(nullGtAddress);
        });

        assertEquals(nullPointerException.getMessage(), "gtAddress " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

}