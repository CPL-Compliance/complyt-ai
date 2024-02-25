package com.complyt.v1.routers;

import testUtils.unit_test.templates.endpoints.GetAllRouterTestTemplate;
import testUtils.unit_test.templates.endpoints.GetByNameRouterTestTemplate;
import testUtils.unit_test.templates.endpoints.GetByTenantIdTestTemplate;
import testUtils.unit_test.templates.endpoints.UpsertByTenantIdRouterTestTemplate;

public interface ClientTrackingRouterTestTemplate extends
        GetAllRouterTestTemplate,
        GetByNameRouterTestTemplate,
        GetByTenantIdTestTemplate,
        UpsertByTenantIdRouterTestTemplate
{
}
