package com.complyt.repositories.internal_rates.criteria;

import org.mockito.junit.jupiter.MockitoExtension;


import com.complyt.domain.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.query.Criteria;
import testUtils.TestUtilities;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountyZipCriteriaBuilderTest {

    @Mock
    private ZipCriteriaBuilder zipCriteriaBuilder;

    @InjectMocks
    private CountyZipCriteriaBuilder countyZipCriteriaBuilder;

    private Address address = TestUtilities.createAddressInCalifornia();

    @Test
    void testBuild_WithZipAndCounty() {
        // Arrange
        Criteria zipCriteria = new Criteria("address.zip").is("12345");
        Address addressWithZipAndCounty = address.withCounty("County");

        when(zipCriteriaBuilder.build(addressWithZipAndCounty.zip())).thenReturn(List.of(zipCriteria));

        // Act
        Criteria result = countyZipCriteriaBuilder.build(addressWithZipAndCounty);

        // Assert
        assertEquals(1, result.getCriteriaObject().size());
    }

    @Test
    void testBuild_WithOnlyZip() {
        // Arrange
        Criteria zipCriteria = new Criteria("address.zip").is("12345");
        address = address.withZip("12345");

        when(zipCriteriaBuilder.build(address.zip())).thenReturn(List.of(zipCriteria));

        // Act
        Criteria result = countyZipCriteriaBuilder.build(address);

        // Assert
        assertEquals(1, result.getCriteriaObject().size());
        assertEquals(zipCriteria.getCriteriaObject(), ((List<?>) result.getCriteriaObject().get("$and")).get(0));
    }

    @Test
    void testBuild_WithNullZip() {
        // Arrange
        Criteria zipCriteria = new Criteria();
        address = address.withZip(null);

        // Act
        Criteria result = countyZipCriteriaBuilder.build(address);

        // Assert
        assertEquals(0, result.getCriteriaObject().size());
    }
}