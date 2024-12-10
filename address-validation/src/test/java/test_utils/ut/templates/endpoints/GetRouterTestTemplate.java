package test_utils.ut.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetRouterTestTemplate {

    @Test
    void get_Exists_Returns200();


    void get_UnauthenticatedUser_Returns401();
    void get_NotFound_Returns400();

    @Test
    void get_InternalServerError_Returns500();

    @Test
    void get_NullHandler_ThrowsNullPointerException();
}
