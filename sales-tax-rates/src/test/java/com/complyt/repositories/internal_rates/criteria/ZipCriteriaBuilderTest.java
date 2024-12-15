package com.complyt.repositories.internal_rates.criteria;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.junit.jupiter.MockitoExtension;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ZipCriteriaBuilderTest {

    private ZipCriteriaBuilder zipCriteriaBuilder;

    @BeforeEach
    void setUp() {
        zipCriteriaBuilder = new ZipCriteriaBuilder();
    }

    @Test
    void testBuild_WithStandardZipCode() {
        // Arrange
        String zip = "12345";

        // Act
        List<Criteria> result = zipCriteriaBuilder.build(zip);

        // Assert
        assertEquals(2, result.size()); // Expect 2 criteria (zip and plus-four checks)
        assertEquals("12345", result.get(0).getCriteriaObject().get("address.zip"));
        assertTrue(result.get(1).getCriteriaObject().containsKey("$and"));
    }

    @Test
    void testBuild_WithZipCodeAndPlusFour() {
        // Arrange
        String zip = "12345-6789";

        // Act
        List<Criteria> result = zipCriteriaBuilder.build(zip);

        // Assert
        assertEquals(2, result.size()); // Expect 2 criteria (zip and plus-four checks)
        assertEquals("12345", result.get(0).getCriteriaObject().get("address.zip"));

        // Validate plus-four range criteria
        Criteria plusFourCriteria = result.get(1);
        List<?> andCriteria = (List<?>) plusFourCriteria.getCriteriaObject().get("$and");
        assertTrue(andCriteria.stream().anyMatch(c -> c.toString().contains("address.lowerPlusFourDigits")));
        assertTrue(andCriteria.stream().anyMatch(c -> c.toString().contains("address.upperPlusFourDigits")));
    }

    @Test
    void testBuild_WithZipNotValid() {
        // Arrange
        String zip = "error";

        // Act
        List<Criteria> result = zipCriteriaBuilder.build(zip);

        // Assert
        assertTrue(result.isEmpty()); // Expect empty criteria list
    }

}