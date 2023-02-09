package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetAllRouterTest {
    @Test
    void getAll_Exists_Returns200WithList();

    @Test
    void getAll_EmptyCollection_Returns200WithEmptyList();

    @Test
    void getAll_UnauthenticatedUser_Returns401();

    @Test
    void getAll_UserWithoutAuthorities_Returns403();

    @Test
    void getAll_InternalServerError_Returns500();

    @Test
    void getAll_NullHandler_ThrowsNullPointerException();
}

