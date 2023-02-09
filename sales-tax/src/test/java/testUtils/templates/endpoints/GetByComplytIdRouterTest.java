package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetByComplytIdRouterTest {
    @Test
    void getByComplytId_Exists_Returns200();

    @Test
    void getByComplytId_DoesntExists_Returns404();

    @Test
    void getByComplytId_UnauthenticatedUser_Returns401();

    @Test
    void getByComplytId_UserWithoutAuthorities_Returns403();

    @Test
    void getByComplytId_InternalServerError_Returns500();

    @Test
    void getByComplytId_NullHandler_ThrowsNullPointerException();
}

