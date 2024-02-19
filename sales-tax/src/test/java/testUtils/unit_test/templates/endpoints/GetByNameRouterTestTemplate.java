package testUtils.unit_test.templates.endpoints;

public interface GetByNameRouterTestTemplate {
    void getByName_Exists_Returns200WithList();

    void getByName_UnauthenticatedUser_Returns401();

    void getByName_PathVariableInvalid_Returns400();

    void getByName_InternalServerError_Returns500();

    void getByName_NullHandler_ThrowsNullPointerException();
}