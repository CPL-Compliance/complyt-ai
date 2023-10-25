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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddressQueryBuilderTest {

    QueryBuilder<Address> addressQueryBuilder;

    Address fullAddress;

    Address partialAddress;

    @BeforeEach
    void setUp() {
        addressQueryBuilder = new AddressQueryBuilder();
        fullAddress = TestUtilities.createAddressInCalifornia();
        partialAddress = fullAddress
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
    void build_FullyAddressPassed_ReturnsFullyAddressQuery() {
        // Given
        Query expectedQuery = Query.query(Criteria
                .where("address.city").regex(fullAddress.city(), "i")
                .and("address.street").regex(fullAddress.street(), "i")
                .and("address.zip").is(fullAddress.zip()));

        // When
        Query actualQuery = addressQueryBuilder.build(fullAddress);

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
