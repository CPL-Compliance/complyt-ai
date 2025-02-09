package integration.endpoints;

import testUtils.integration_test.templates.endpoints.*;

public interface SalesTaxTrackingEndpointsITemplate extends
        UpsertByStateITTemplate,
        GetByStateITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        PatchITTemplate,
        RefreshITTemplate {

    void upsertByCountryAndState_NonUsaCountryDoesntExists_Returns201();

    void upsertByCountryAndState_NonUsaCountryAbbreviation_Returns200();

    void getByCountry_Exists_Returns200();

    void getByCountryAbbreviation_Exists_Returns200();

    void refreshByCountryAndDate_NonUsaCountryExistsAndHasNexus_Returns200NoSummary();

    void upsertByCountryAndState_StateIsNull_Returns400();

    void upsertByCountryAndState_CountryIsNull_Returns400();

    void upsertByCountryAndState_UsaAndStateIsDifferentInBody_Returns400ConflictedData();

    void upsertByCountryAndState_CountryInQueryAndBodyAreDifferent_Returns400ConflictedData();

    void getByCountryStateAndSubsidiary_DoesntExists_Returns404();

    void getByCountryStateAndSubsidiary_Exists_Returns200();

    void path_AppliedDateIsWrongFormat_Returns400();

    void path_ApprovalDateIsWrongFormat_Returns400();

    void path_RegistrationDateIsWrongFormat_Returns400();
}