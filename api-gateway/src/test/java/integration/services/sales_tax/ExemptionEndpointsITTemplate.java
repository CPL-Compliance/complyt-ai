package integration.services.sales_tax;

import integration.test_utils.templates.methods.PatchITTemplate;

public interface ExemptionEndpointsITTemplate extends
        PatchITTemplate {
    void upsert_UsaCountryWithState_Return200(); //todo: fix - look at impl
//    void upsert_NoUsaCountry_Return200(); todo: fix - look at impl
//    void upsert_UsaCountryNoState_Return400();
}
