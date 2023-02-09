package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetByNameRouterTest {
    @Test
    void getByName_Exists_Returns200WithList();

    @Test
    void getByName_EmptyCollection_Returns200WithEmptyList();

    @Test
    void getByName_UnauthenticatedUser_Returns401();

    @Test
    void getByName_UserWithoutAuthorities_Returns403();

    @Test
    void getByName_InternalServerError_Returns500();

    @Test
    void getByName_NullHandler_ThrowsNullPointerException();
}

