package com.complyt.v1.routers;

import testUtils.templates.endpoints.GetAllRouterTest;
import testUtils.templates.endpoints.GetByComplytIdRouterTest;
import testUtils.templates.endpoints.GetByStateRouterTest;
import testUtils.templates.endpoints.UpsertByStateRouterTest;

public interface SalesTaxTrackingRouterTest extends
        GetAllRouterTest,
        GetByComplytIdRouterTest,
        GetByStateRouterTest,
        // Validation::State, ComplytId
        UpsertByStateRouterTest {
    
}
