package testUtils.templates.endpoints;

public interface GetByNameRouterTestTemplate {
    void getByName_Exists_Returns200WithList();

    void getByName_EmptyCollection_Returns200WithEmptyList();

    void getByName_UnauthenticatedUser_Returns401();

    void getByName_UserWithoutAuthorities_Returns403();

    void getByName_InternalServerError_Returns500();

    void getByName_NullHandler_ThrowsNullPointerException();
}

