package integration.endpoints;

import testUtils.integration_test.templates.endpoints.GetAllITTemplate;
import testUtils.integration_test.templates.endpoints.GetByNameITTemplate;
import testUtils.unit_test.templates.endpoints.GetByTenantIdTestTemplate;

public interface ClientTrackingEndpointsITTemplate extends
        GetAllITTemplate,
        GetByNameITTemplate,
        GetByTenantIdTestTemplate {
}
