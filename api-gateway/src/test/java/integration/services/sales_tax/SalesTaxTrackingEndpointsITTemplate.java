package integration.services.sales_tax;


import integration.test_utils.templates.endpoints.GetAllITTemplate;
import integration.test_utils.templates.endpoints.GetByComplytIdITTemplate;
import integration.test_utils.templates.endpoints.GetByStateITTemplate;
import integration.test_utils.templates.endpoints.UpsertByStateITTemplate;
import integration.test_utils.templates.methods.PatchITTemplate;
import integration.test_utils.templates.methods.PostITTemplate;

public interface SalesTaxTrackingEndpointsITTemplate extends
        UpsertByStateITTemplate,
        GetByStateITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        PostITTemplate,
        PatchITTemplate {

    void refresh_EverythingExists_Returns200WithSummary();
}
