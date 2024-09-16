package integration.endpoints;

import testUtils.integration_test.templates.endpoints.GetAllITTemplate;
import testUtils.integration_test.templates.endpoints.GetByNameITTemplate;
import testUtils.integration_test.templates.endpoints.GetByTenantIdITTemplate;
import testUtils.integration_test.templates.endpoints.UpsertByTenantIdITTemplate;

public interface ClientTrackingEndpointsITTemplate extends
        GetAllITTemplate,
        GetByNameITTemplate,
        GetByTenantIdITTemplate,
        UpsertByTenantIdITTemplate {
}
