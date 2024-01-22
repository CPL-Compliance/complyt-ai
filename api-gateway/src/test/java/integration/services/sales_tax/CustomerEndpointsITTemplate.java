package integration.services.sales_tax;

import integration.test_utils.templates.endpoints.*;

public interface CustomerEndpointsITTemplate extends
        UpsertByExternalIdAndSourceITTemplate,
        GetByExternalIdAndSourceITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        GetAllBySourceTTemplate {
}
