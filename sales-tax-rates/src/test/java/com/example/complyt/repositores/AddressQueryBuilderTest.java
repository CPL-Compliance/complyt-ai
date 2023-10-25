package com.example.complyt.repositores;

import com.complyt.domain.Address;
import com.complyt.repositories.AddressQueryBuilder;
import com.complyt.repositories.QueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import testUtils.TestUtilities;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddressQueryBuilderTest {

    QueryBuilder<Address> addressQueryBuilder;

    Address fullAddressNoCountyAddress;
    Address fullAddresswithCountyAddress;
    Address partialAddress;

    @BeforeEach
    void setUp() {
        addressQueryBuilder = new AddressQueryBuilder();
        fullAddressNoCountyAddress = TestUtilities.createAddressInCalifornia();
        fullAddresswithCountyAddress = fullAddressNoCountyAddress.withCounty("county");
        partialAddress = fullAddressNoCountyAddress
                .withPartial(true)
                .withCity(null)
                .withStreet(null);
    }

    @Test
    void build_PartialAddressPassed_ReturnsPartialAddressQuery() {
        // Given
        Query expectedQuery = Query.query(Criteria.where("address.zip").is(partialAddress.zip()));

        // When
        Query actualQuery = addressQueryBuilder.build(partialAddress);

        // Then
        Assertions.assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void build_AddressPassed_ReturnsQuery() {
        // Given
        Query expectedQuery = Query.query(Criteria.where("address.zip").is(fullAddressNoCountyAddress.zip()));
        Optional.ofNullable(fullAddressNoCountyAddress.city())
                .ifPresent(value -> expectedQuery.addCriteria(Criteria.where("address.city").regex(value, "i")));
        Optional.ofNullable(fullAddressNoCountyAddress.street())
                .ifPresent(value -> expectedQuery.addCriteria(Criteria.where("address.street").regex(value, "i")));
        Optional.ofNullable(fullAddressNoCountyAddress.county())
                .ifPresent(value -> expectedQuery.addCriteria(Criteria.where("address.county").regex(value, "i")));

        // When
        Query actualQuery = addressQueryBuilder.build(fullAddressNoCountyAddress);

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
