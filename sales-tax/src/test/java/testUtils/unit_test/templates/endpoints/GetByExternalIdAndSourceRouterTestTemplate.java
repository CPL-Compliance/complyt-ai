package testUtils.unit_test.templates.endpoints;

public interface GetByExternalIdAndSourceRouterTestTemplate {
    void getByExternalIdAndSource_Exists_Returns200();

    void getByExternalIdAndSource_DoesntExists_Returns404();

    void getByExternalIdAndSource_UnauthenticatedUser_Returns401();

    void getByExternalIdAndSource_UserWithoutAuthorities_Returns403();

    void getByExternalIdAndSource_InternalServerError_Returns500();

    void getByExternalIdAndSource_NullHandler_ThrowsNullPointerException();
}

