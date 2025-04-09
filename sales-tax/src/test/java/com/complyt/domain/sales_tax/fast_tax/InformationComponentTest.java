package com.complyt.domain.sales_tax.fast_tax;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class InformationComponentTest {

    private InformationComponent informationComponent;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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
        String expectedString = "InformationComponent(name=" + informationComponent.getName() +
                ", value=" + informationComponent.getValue() + ")";

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