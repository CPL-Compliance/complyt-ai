package io.complyt.v1.routers;

import org.junit.jupiter.api.Test;
import test_utils.ut.templates.endpoints.GetRouterTestTemplate;
import test_utils.ut.templates.validations.AddressValidationTestTemplate;

interface ValidAddressHandlerTestTemplate  extends
        GetRouterTestTemplate,
        AddressValidationTestTemplate {

    @Test
    void getAny_InvalidUrl_Returns404();
}