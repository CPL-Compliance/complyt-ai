package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface DeleteByExternalIdAndSourceRouterTest {
    @Test
    void deleteByExternalIdAndSource_Exists_Returns204();

    @Test
    void deleteByExternalIdAndSource_DoesntExists_Returns404();

    @Test
    void deleteByExternalIdAndSource_UnauthenticatedUser_Returns401();

    @Test
    void deleteByExternalIdAndSource_UserWithoutAuthorities_Returns403();

    @Test
    void deleteByExternalIdAndSource_UserWithoutCSRFToken_Returns403();

    @Test
    void deleteByExternalIdAndSource_InternalServerError_Returns500();

    @Test
    void deleteByExternalIdAndSource_NullHandler_ThrowsNullPointerException();
}

