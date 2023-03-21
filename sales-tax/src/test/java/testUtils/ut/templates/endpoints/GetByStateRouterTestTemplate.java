package testUtils.ut.templates.endpoints;

public interface GetByStateRouterTestTemplate {
    void getByState_Exists_Returns200WithList();

    void getByState_DoesntExists_Returns404();

    void getByState_UnauthenticatedUser_Returns401();

    void getByState_UserWithoutAuthorities_Returns403();

    void getByState_InternalServerError_Returns500();

    void getByState_NullHandler_ThrowsNullPointerException();
}

