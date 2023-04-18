package integration.endpoints;

import testUtils.integration_test.templates.endpoints.GetAllITTemplate;
import testUtils.integration_test.templates.endpoints.GetByComplytIdITTemplate;
import testUtils.integration_test.templates.endpoints.GetByStateITTemplate;
import testUtils.integration_test.templates.endpoints.UpsertByStateITTemplate;

public interface SalesTaxTrackingEndpointsITTemplate extends
        UpsertByStateITTemplate,
        GetByStateITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate {
}
