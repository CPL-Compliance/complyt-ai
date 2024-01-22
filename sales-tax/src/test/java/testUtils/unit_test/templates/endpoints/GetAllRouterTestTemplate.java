package testUtils.unit_test.templates.endpoints;

public interface GetAllRouterTestTemplate {
    void getAll_Exists_Returns200WithList();

    void getAll_QueryParamInvalid_Returns400();

    void getAll_EmptyCollection_Returns200WithEmptyList();

    void getAll_UnauthenticatedUser_Returns401();

    void getAll_UserWithoutAuthorities_Returns403();

    void getAll_InternalServerError_Returns500();

    void getAll_NullHandler_ThrowsNullPointerException();
}

