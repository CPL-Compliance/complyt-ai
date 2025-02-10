package io.complyt.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import test_utils.TestUtilities;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class UnitedStatesAddressQueryBuilderTest {
    UnitedStatesAddressQueryBuilder addressQueryBuilder;

    Address fullAddressNoCountyAddress;
    Address fullAddressWithCountyAddress;
    Address partialAddress;

    @BeforeEach
    void setUp() {
        addressQueryBuilder = new UnitedStatesAddressQueryBuilder();
        fullAddressNoCountyAddress = TestUtilities.getAddress();
        fullAddressWithCountyAddress = fullAddressNoCountyAddress.withCounty("county");
        partialAddress = fullAddressNoCountyAddress
                .withIsPartial(true)
                .withCity(null)
                .withStreet(null);
    }

    @Test
    void build_PartialAddressPassed_ReturnsPartialAddressQuery() {
        // Given
        Query expectedQuery = Query.query(Criteria.where("requestAddress.zip").is(partialAddress.zip()));

        // When
        Query actualQuery = addressQueryBuilder.build(partialAddress);

        // Then
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void build_AddressPassed_ReturnsQuery() {
        // Given
        Query expectedQuery = Query.query(Criteria.where("requestAddress.zip").is(fullAddressNoCountyAddress.zip()));

        Optional.ofNullable(fullAddressNoCountyAddress.city()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            expectedQuery.addCriteria(Criteria.where("requestAddress.city").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(fullAddressNoCountyAddress.street()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            expectedQuery.addCriteria(Criteria.where("requestAddress.street").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(fullAddressNoCountyAddress.county()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            expectedQuery.addCriteria(Criteria.where("requestAddress.county").regex(escapedSearchString, "i"));
        });

        // When
        Query actualQuery = addressQueryBuilder.build(fullAddressNoCountyAddress);

        // Then
        Assertions.assertEquals(expectedQuery, actualQuery);
    }


    @Test
    void build_FullAddressWithCountyPassed_ReturnsQuery() {
        // Given
        Query expectedQuery = Query.query(Criteria.where("requestAddress.zip").is(fullAddressNoCountyAddress.zip()));

        Optional.ofNullable(fullAddressWithCountyAddress.city()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            expectedQuery.addCriteria(Criteria.where("requestAddress.city").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(fullAddressWithCountyAddress.street()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            expectedQuery.addCriteria(Criteria.where("requestAddress.street").regex(escapedSearchString, "i"));
        });

        Optional.ofNullable(fullAddressWithCountyAddress.county()).ifPresent(value -> {
            String escapedSearchString = Pattern.quote(value);
            expectedQuery.addCriteria(Criteria.where("requestAddress.county").regex(escapedSearchString, "i"));
        });

        // When
        Query actualQuery = addressQueryBuilder.build(fullAddressWithCountyAddress);

        // Then
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void build_NullAddressPassed_ThrowsException() {
        // Given
        Address nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            addressQueryBuilder.build(nullAddress);
        });

        assertEquals(nullPointerException.getMessage(), "address " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }
}