package integration.endpoints;

import testUtils.it.templates.endpoints.*;

public interface CustomerEndpointsITTemplate extends
        UpsertByExternalIdAndSourceITTemplate,
        GetByExternalIdAndSourceITTemplate,
        GetByComplytIdITTemplate,
        GetByNameITTemplate,
        GetAllITTemplate,
        GetAllBySourceTTemplate {
}
