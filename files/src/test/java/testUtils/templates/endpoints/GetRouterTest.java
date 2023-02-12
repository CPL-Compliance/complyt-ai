package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetRouterTest {

    @Test
    void get_Exists_Returns200();

    @Test
    void get_DoesntExist_Returns404();

    @Test
    void get_UnauthenticatedUser_Returns401();

    @Test
    void get_UserWithoutAuthorities_Returns403();

    @Test
    void get_InternalServerError_Returns500();

    @Test
    void get_NullHandler_ThrowsNullPointerException();
}
