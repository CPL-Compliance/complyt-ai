package integration.endpoints;

import testUtils.integration_test.templates.endpoints.GetAllITTemplate;
import testUtils.integration_test.templates.endpoints.GetByNameITTemplate;
import testUtils.integration_test.templates.endpoints.GetByTenantIdITTemplate;
import testUtils.integration_test.templates.endpoints.UpsertByTenantIdITTemplate;
import testUtils.unit_test.templates.endpoints.GetByTenantIdTestTemplate;
import testUtils.unit_test.templates.endpoints.UpsertByTenantIdRouterTestTemplate;

public interface ClientTrackingEndpointsITTemplate extends
        GetAllITTemplate,
        GetByNameITTemplate,
        GetByTenantIdITTemplate,
        UpsertByTenantIdITTemplate {
}
