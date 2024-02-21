package testUtils.unit_test.templates.endpoints;

public interface GetByTenantIdTestTemplate {
    void getByTenantId_Exists_Returns200WithList();

    void getByTenantId_PathVariableInvalid_Returns400();

    void getByTenantId_UnauthenticatedUser_Returns401();

    void getByTenantId_InternalServerError_Returns500();

    void getByTenantId_NullHandler_ThrowsNullPointerException();
}
