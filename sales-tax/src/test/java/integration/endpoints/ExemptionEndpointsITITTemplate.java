package integration.endpoints;

import testUtils.integration_test.templates.endpoints.GetByComplytIdITTemplate;
import testUtils.integration_test.templates.endpoints.PatchITTemplate;

public interface ExemptionEndpointsITITTemplate extends
        PatchITTemplate, GetByComplytIdITTemplate {

    void update_UpdatesExemption_ReturnsExemptionWithCustomer();

    void update_CustomerNotFound_Throws404NotFound();
}
