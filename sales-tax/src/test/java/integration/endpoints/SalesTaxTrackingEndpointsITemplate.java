package integration.endpoints;

import testUtils.integration_test.templates.endpoints.*;

public interface SalesTaxTrackingEndpointsITemplate extends
        UpsertByStateITTemplate,
        GetByStateITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        PatchITTemplate {

    // Refresh By State And date
    void refreshByStateAndDate_Exists_Returns200WithSummaryAndNewNexusRule();

    void refreshByStateAndDate_ExistsAndHasNexus_Returns200NoSummary();

    void refreshByStateAndDate_DoesntExists_Returns404NotFound();

    void refreshByStateAndDate_DoesNotPassValidation_Returns400();
}
