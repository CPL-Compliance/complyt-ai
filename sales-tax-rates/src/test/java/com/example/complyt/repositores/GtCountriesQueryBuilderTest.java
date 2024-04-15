package com.example.complyt.repositores;

import com.complyt.domain.gt.GtAddress;
import com.complyt.repositories.GtCountriesQueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import testUtils.TestUtilities;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GtCountriesQueryBuilderTest {

    GtCountriesQueryBuilder gtCountriesQueryBuilder;

    @BeforeEach
    void setUp() {
        gtCountriesQueryBuilder = new GtCountriesQueryBuilder();
    }

    @Test
    void build_GtAddressPassed_ReturnsAddressQuery() {
        // Given + When
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress();
        Query expectedQuery = Query.query(Criteria.where("gtAddress.country").regex(gtAddress.country(), "i"));

        Optional.ofNullable(gtAddress.region()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            expectedQuery.addCriteria(Criteria.where("gtAddress.region").regex(escapedSearchString, "i"));
        });
        Query actualQuery = gtCountriesQueryBuilder.build(gtAddress);

        // Then
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void build_GtAddressPassedWithNullRegion_ReturnsAddressQuery() {
        // Given + When
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress().withRegion(null);
        Query expectedQuery = Query.query(Criteria.where("gtAddress.country").regex(gtAddress.country(), "i"));

        expectedQuery.addCriteria(Criteria.where("gtAddress.region").exists(false));
        Query actualQuery = gtCountriesQueryBuilder.build(gtAddress);

        // Then
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void build_NullAddressPassed_ThrowsException() {
        // Given
        GtAddress nullGtAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            gtCountriesQueryBuilder.build(nullGtAddress);
        });

        assertEquals(nullPointerException.getMessage(), "gtAddress " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }
}
