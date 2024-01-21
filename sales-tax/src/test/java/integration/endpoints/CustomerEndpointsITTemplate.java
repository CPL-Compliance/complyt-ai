package integration.endpoints;

import testUtils.integration_test.templates.endpoints.*;

public interface CustomerEndpointsITTemplate extends
        UpsertByExternalIdAndSourceITTemplate,
        GetByExternalIdAndSourceITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        GetAllBySourceTTemplate {
}
