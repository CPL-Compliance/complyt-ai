package integration;

import testUtils.it.templates.GetAllBySourceTTemplate;
import testUtils.it.templates.GetAllITTemplate;
import testUtils.it.templates.GetByExternalIdAndSourceITTemplate;
import testUtils.it.templates.UpsertByExternalIdAndSourceITTemplate;

public interface TransactionApiITTemplate extends
        UpsertByExternalIdAndSourceITTemplate,
        GetByExternalIdAndSourceITTemplate,
        GetAllITTemplate,
        GetAllBySourceTTemplate
{

    void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404();
    void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns500();
}
