package testUtils.unit_test.templates.endpoints;

public interface UpsertByComplytIdRouterTestTemplate {
    void upsertByComplytId_Exists_Returns200();

    void upsertByComplytId_PathVariableInvalid_Returns400();

    void upsertByComplytId_DoesntExists_Returns404();

    void upsertByComplytId_CoupleValidationsFailure_Returns400WithErrorList();

    void upsertByComplytId_DifferentComplytIdInBody_Returns400ConflictedData();

    void upsertByComplytId_NullComplytId_Returns400ValidationError();

    void upsertByComplytId_BlankComplytId_Returns400ValidationError();

    void upsertByComplytId_ComplytIdFailedToParse_Returns400();

    void upsertByComplytId_UnauthenticatedUser_Returns401();

    void upsertByComplytId_UserWithoutAuthorities_Returns403();

    void upsertByComplytId_UserWithoutCSRFToken_Returns403();

    void upsertByComplytId_InternalServerError_Returns500();

    void upsertByComplytId_NullHandler_ThrowsNullPointerException();

    void upsertByComplytId_NoBody_Returns400();
}

