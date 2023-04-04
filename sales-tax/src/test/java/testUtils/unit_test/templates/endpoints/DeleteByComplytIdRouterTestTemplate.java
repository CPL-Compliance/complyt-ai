package testUtils.unit_test.templates.endpoints;

public interface DeleteByComplytIdRouterTestTemplate {
    void deleteByComplytId_Exists_Returns204();

    void deleteByComplytId_DoesntExists_Returns404();

    void deleteByComplytId_UnauthenticatedUser_Returns401();

    void deleteByComplytId_UserWithoutAuthorities_Returns403();

    void deleteByComplytId_UserWithoutCSRFToken_Returns403();

    void deleteByComplytId_InternalServerError_Returns500();

    void deleteByComplytId_NullHandler_ThrowsNullPointerException();
}

