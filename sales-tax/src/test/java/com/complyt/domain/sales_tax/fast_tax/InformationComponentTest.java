package com.complyt.domain.sales_tax.fast_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InformationComponentTest {

    private InformationComponent informationComponent;

    @BeforeEach
    void setup() {
        informationComponent = new InformationComponent("name", "value");
    }

    @Test
    void Equals_sameInformationComponent_ReturnsTrue() {
        // Given
        InformationComponent givenInformationComponent = new InformationComponent("name", "value");

        // When
        boolean isEquals = informationComponent.equals(givenInformationComponent);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "InformationComponent(name=name, value=value)";

        // When
        String actualString = informationComponent.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void noArgsConstructor_ReturnEmptyInformationComponent() {
        // Given
        InformationComponent expectedInformationComponent = new InformationComponent(null, null);

        // When
        InformationComponent actualInformationComponent = new InformationComponent();

        // Then
        assertEquals(expectedInformationComponent, actualInformationComponent);
    }

}