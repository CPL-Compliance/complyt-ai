package testUtils.unit_test.templates.endpoints;

public interface PostByStateRouterTestTemplate {
    void postByStateName_Exists_Returns200();

    void postByStateAbbreviation_Exists_Returns200();

    void postByState_DoesntExists_Returns201();

    void postByState_UnauthenticatedUser_Returns401();

    void postByState_UserWithoutAuthorities_Returns403();

    void postByState_UserWithoutCSRFToken_Returns403();

    void postByState_InternalServerError_Returns500();

    void postByState_NullHandler_ThrowsNullPointerException();
}

