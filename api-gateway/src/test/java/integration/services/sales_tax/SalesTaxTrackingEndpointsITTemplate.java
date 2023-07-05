package integration.services.sales_tax;


import integration.test_utils.templates.endpoints.GetAllITTemplate;
import integration.test_utils.templates.endpoints.GetByComplytIdITTemplate;
import integration.test_utils.templates.endpoints.GetByStateITTemplate;
import integration.test_utils.templates.endpoints.UpsertByStateITTemplate;

public interface SalesTaxTrackingEndpointsITTemplate extends
        UpsertByStateITTemplate,
        GetByStateITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate {
}
