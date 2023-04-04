package testUtils.unit_test.templates.endpoints;

public interface GetAllBySourceRouterTestTemplate {
    void getAllBySource_Exists_Returns200WithList();

    void getAllBySource_EmptyCollection_Returns200WithEmptyList();

    void getAllBySource_UnauthenticatedUser_Returns401();

    void getAllBySource_UserWithoutAuthorities_Returns403();

    void getAllBySource_InternalServerError_Returns500();

    void getAllBySource_NullHandler_ThrowsNullPointerException();
}

