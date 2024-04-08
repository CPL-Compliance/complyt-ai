package integration.services.sales_tax;


import integration.test_utils.templates.endpoints.GetAllITTemplate;
import integration.test_utils.templates.endpoints.GetByComplytIdITTemplate;
import integration.test_utils.templates.endpoints.GetByStateITTemplate;
import integration.test_utils.templates.endpoints.UpsertByStateITTemplate;
import integration.test_utils.templates.methods.PatchITTemplate;
import integration.test_utils.templates.methods.PostITTemplate;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public interface SalesTaxTrackingEndpointsITTemplate extends
        UpsertByStateITTemplate,
        GetByStateITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        PostITTemplate,
        PatchITTemplate {

    void refresh_UsaEverythingExists_Returns200WithSummary();

    void refresh_NonUsaEverythingExists_Returns200WithSummary();

    @Order(6)
    @Test
    void getOne_NonUsaEverythingExists_Returns200WithSummary();

    void upsertByCountryAndState_UsaCountryNoState_Returns400();

    void upsertByCountryAndState_NonUsaCountryNonSupportedCountry_Returns400();

    void upsertByCountry_NonUsaCountry_Returns200();
}
