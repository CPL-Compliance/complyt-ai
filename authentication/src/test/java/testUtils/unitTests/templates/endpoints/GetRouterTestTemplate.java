package testUtils.unitTests.templates.endpoints;

public interface GetRouterTestTemplate {
    void get_Exists_Returns200WithList();

    void get_EmptyCollection_Returns200WithEmptyList();

    void get_UnauthenticatedUser_Returns401();

    void get_UserWithoutAuthorities_Returns403();

    void get_InternalServerError_Returns500();

    void get_NullHandler_ThrowsNullPointerException();
}

