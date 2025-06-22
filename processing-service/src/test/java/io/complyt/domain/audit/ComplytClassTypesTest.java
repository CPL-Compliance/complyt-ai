package io.complyt.domain.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComplytClassTypesTest {

    @Test
    public void complytClassType_Customer_ReturnsCustomer() {
        // Given + When
        ComplytClassTypes complytClassType = ComplytClassTypes.CUSTOMER;

        // Then
        assertEquals(complytClassType, ComplytClassTypes.valueOf("CUSTOMER"));
    }

    @Test
    public void complytClassType_Transaction_ReturnsTransaction() {
        // Given + When
        ComplytClassTypes complytClassType = ComplytClassTypes.TRANSACTION;

        // Then
        assertEquals(complytClassType, ComplytClassTypes.valueOf("TRANSACTION"));
    }

    @Test
    public void complytClassType_SalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given + When
        ComplytClassTypes complytClassType = ComplytClassTypes.SALES_TAX_TRACKING;

        // Then
        assertEquals(complytClassType, ComplytClassTypes.valueOf("SALES_TAX_TRACKING"));
    }

}