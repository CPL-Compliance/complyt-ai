package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface UpsertByComplytIdRouterTest {
    @Test
    void upsertByComplytId_Exists_Returns200();

    @Test
    void upsertByComplytId_DoesntExists_Returns404();

    @Test
    void upsertByComplytId_CoupleValidationsFailure_Returns400WithErrorList();

    @Test
    void upsertByComplytId_DifferentComplytIdInBody_Returns400ConflictedData();

    @Test
    void upsertByComplytId_NullComplytId_Returns400ValidationError();

    @Test
    void upsertByComplytId_BlankComplytId_Returns400ValidationError();

    @Test
    void upsertByComplytId_ComplytIdFailedToParse_Returns400();

    @Test
    void upsertByComplytId_UnauthenticatedUser_Returns401();

    @Test
    void upsertByComplytId_UserWithoutAuthorities_Returns403();

    @Test
    void upsertByComplytId_UserWithoutCSRFToken_Returns403();

    @Test
    void upsertByComplytId_InternalServerError_Returns500();

    @Test
    void upsertByComplytId_NullHandler_ThrowsNullPointerException();
}

