package testUtils.templates.endpoints;

public interface CreateRouterTest {
    void createByComplytId_Exists_Returns201();

    void createByComplytId_CoupleValidationsFailure_Returns400WithErrorList();

    void createByComplytId_UnauthenticatedUser_Returns401();

    void createByComplytId_UserWithoutAuthorities_Returns403();

    void createByComplytId_UserWithoutCSRFToken_Returns403();

    void createByComplytId_InternalServerError_Returns500();

    void createByComplytId_NullHandler_ThrowsNullPointerException();
}

