package integration.services.sales_tax;

import integration.test_utils.templates.endpoints.*;
import integration.test_utils.templates.methods.PatchITTemplate;

public interface CustomerEndpointsITTemplate extends
        UpsertByExternalIdAndSourceITTemplate,
        GetByExternalIdAndSourceITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        GetAllBySourceTTemplate,
        PatchITTemplate {
}
