package com.example.complyt.domain.fast_tax;

import com.complyt.domain.fast_tax.InformationComponent;
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
        String expectedString = "InformationComponent[name=" + informationComponent.name() +
                ", value=" + informationComponent.value() + "]";

        // When
        String actualString = informationComponent.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}