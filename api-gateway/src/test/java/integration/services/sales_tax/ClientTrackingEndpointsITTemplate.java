package integration.services.sales_tax;

import integration.test_utils.templates.endpoints.GetAllITTemplate;
import integration.test_utils.templates.endpoints.GetByNameITTemplate;
import integration.test_utils.templates.endpoints.GetByTenantIdITTemplate;
import integration.test_utils.templates.endpoints.UpsertByTenantIdITTemplate;

public interface ClientTrackingEndpointsITTemplate extends
        GetAllITTemplate,
        GetByNameITTemplate,
        GetByTenantIdITTemplate,
        UpsertByTenantIdITTemplate {
}
