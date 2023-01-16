package com.complyt.v1.routers;

import com.complyt.v1.handlers.SalesTaxTrackingHandler;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SalesTaxTrackingRouterTest {

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
