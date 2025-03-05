package com.complyt.repositories.internal_rates;

import com.complyt.domain.Address;

import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.repositories.internal_rates.address_standardization.StandardizeAddress;
import com.complyt.repositories.internal_rates.criteria.CountyZipCriteriaBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import testUtils.TestUtilities;

import java.time.LocalDateTime;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalRatesAddressQueryBuilderTest {

    @Mock
    private StandardizeAddress standardizeAddress;

    @Mock
    private CountyZipCriteriaBuilder countyCityZipCriteriaBuilder;

    @InjectMocks
    private InternalRatesAddressQueryBuilder internalRatesAddressQueryBuilder;

    private Address address;
    private InternalSalesTaxRates internalSalesTaxRates;

    @BeforeEach
    void setUp() {
        address = TestUtilities.createAddressInCalifornia();
        internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now());
    }

    @Test
    void testBuild_WithStandardizedAddress() {
        // Arrange
        Criteria criteria = new Criteria("address.zip").is("12345");
        when(standardizeAddress.standardize(address)).thenReturn(address);
        when(countyCityZipCriteriaBuilder.build(address)).thenReturn(criteria);

        // Act
        Query result = internalRatesAddressQueryBuilder.build(address);

        // Assert
        assertEquals(Query.query(criteria), result);
    }

    @Test
    void testBuild_NullAddress_ShouldThrowNullPointerException() {
        // Act & Assert
        InternalSalesTaxRates internalSalesTaxRates = null;
        assertThrows(NullPointerException.class, () -> internalRatesAddressQueryBuilder.build(internalSalesTaxRates));
    }

    @Test
    void testBuild_InternalSalesTaxRates() {
        // Arrange
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("address.zip").is(internalSalesTaxRates.getAddress().zip()),
                Criteria.where("address.lowerPlusFourDigits").is(internalSalesTaxRates.getAddress().lowerPlusFourDigits()),
                Criteria.where("address.upperPlusFourDigits").is(internalSalesTaxRates.getAddress().upperPlusFourDigits())
        );

        // Act
        Query result = internalRatesAddressQueryBuilder.build(internalSalesTaxRates);

        // Assert
        assertEquals(Query.query(criteria), result);
    }

    @Test
    void testBuild_NullInternalRate_ShouldThrowNullPointerException() {
        // Act & Assert
        Address nullAddress = null;
        assertThrows(NullPointerException.class, () -> internalRatesAddressQueryBuilder.build(nullAddress));
    }
}