package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetByComplytIdRouterTest {
    void getByComplytId_Exists_Returns200();

    void getByComplytId_DoesntExists_Returns404();

    void getByComplytId_UnauthenticatedUser_Returns401();

    void getByComplytId_UserWithoutAuthorities_Returns403();

    void getByComplytId_InternalServerError_Returns500();

    void getByComplytId_NullHandler_ThrowsNullPointerException();
}

