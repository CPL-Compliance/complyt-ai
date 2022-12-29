package com.complyt.v1.controllers.router;

import com.complyt.v1.controllers.router.handler.SalesTaxTrackingHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SalesTaxRouterTest {

    SalesTaxTrackingRouter salesTaxTrackingRouter;

    @BeforeEach
    void setUp() {
        salesTaxTrackingRouter = new SalesTaxTrackingRouter();
    }

    @Test
    void exemptionRoute_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        SalesTaxTrackingHandler nullSalesTaxTrackingHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingRouter.salesTaxTrackingRoute(nullSalesTaxTrackingHandler);
        });

        // Then
        assertEquals("salesTaxTrackingHandler is marked non-null but is null", nullPointerException.getMessage());
    }
}
