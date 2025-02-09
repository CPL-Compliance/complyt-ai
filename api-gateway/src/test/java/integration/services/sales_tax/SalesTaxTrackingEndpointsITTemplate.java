package integration.services.sales_tax;


import integration.test_utils.templates.endpoints.GetAllITTemplate;
import integration.test_utils.templates.endpoints.GetByComplytIdITTemplate;
import integration.test_utils.templates.endpoints.GetByStateITTemplate;
import integration.test_utils.templates.endpoints.UpsertByStateITTemplate;
import integration.test_utils.templates.methods.PatchITTemplate;
import integration.test_utils.templates.methods.PostITTemplate;
import integration.test_utils.templates.methods.RefreshITTemplate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public interface SalesTaxTrackingEndpointsITTemplate extends
        UpsertByStateITTemplate,
        GetByStateITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        PostITTemplate,
        PatchITTemplate,
        RefreshITTemplate {

    @Order(6)
    @Test
    void getOne_NonUsaEverythingExists_Returns200WithSummary();

    void upsertByCountryAndState_UsaCountryNoState_Returns400();

    void upsertByCountryAndState_NonUsaCountryNonSupportedCountry_Returns400();

    void upsertByCountry_NonUsaCountry_Returns200();

    void path_AppliedDateIsWrongFormat_Returns400();

    void path_ApprovalDateIsWrongFormat_Returns400();

    void path_RegistrationDateIsWrongFormat_Returns400();
}
