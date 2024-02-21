package test_utils.unit_tests.templates;

import org.junit.jupiter.api.Test;

public interface DeleteRouterTestMonoTemplate {

    @Test
    void delete_SentAsFormURLEncoded_Exists_Returns204();

    @Test
    void delete_SentAsJson_Exists_Returns204();

    @Test
    void delete_DoesntExist_Returns204();

    @Test
    void delete_InternalServerError_ReturnInternalServerError();

    @Test
    void delete_NullHandler_ThrowsNullPointerException();

    @Test
    void delete_UnauthenticatedUser_Returns401();
    @Test
    void delete_missingCsrfToken_return403();

}
