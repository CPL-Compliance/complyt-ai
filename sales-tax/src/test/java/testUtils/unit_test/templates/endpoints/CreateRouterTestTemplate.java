package testUtils.unit_test.templates.endpoints;

public interface CreateRouterTestTemplate {

    void createByComplytId_CoupleValidationsFailure_Returns400WithErrorList();

    void createByComplytId_UnauthenticatedUser_Returns401();

    void createByComplytId_UserWithoutAuthorities_Returns403();

    void createByComplytId_UserWithoutCSRFToken_Returns403();

    void createByComplytId_InternalServerError_Returns500();

    void createByComplytId_NullHandler_ThrowsNullPointerException();
}

