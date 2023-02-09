package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetAllBySourceRouterTest {
    @Test
    void getAllBySource_Exists_Returns200WithList();

    @Test
    void getAllBySource_EmptyCollection_Returns200WithEmptyList();

    @Test
    void getAllBySource_UnauthenticatedUser_Returns401();

    @Test
    void getAllBySource_UserWithoutAuthorities_Returns403();

    @Test
    void getAllBySource_InternalServerError_Returns500();

    @Test
    void getAllBySource_NullHandler_ThrowsNullPointerException();
}

