package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetByExternalIdAndSourceRouterTest {
    @Test
    void getByExternalIdAndSource_Exists_Returns200();

    @Test
    void getByExternalIdAndSource_DoesntExists_Returns404();

    @Test
    void getByExternalIdAndSource_UnauthenticatedUser_Returns401();

    @Test
    void getByExternalIdAndSource_UserWithoutAuthorities_Returns403();

    @Test
    void getByExternalIdAndSource_UserWithoutCSRFToken_Returns403();

    @Test
    void getByExternalIdAndSource_InternalServerError_Returns500();

    @Test
    void getByExternalIdAndSource_NullHandler_ThrowsNullPointerException();
}

