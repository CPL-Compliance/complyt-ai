package testUtils.ut.templates.endpoints;

public interface UpsertByStateRouterTestTemplate {
    void upsertByStateName_Exists_Returns200();

    void upsertByStateAbbreviation_Exists_Returns200();

    void upsertByState_DoesntExists_Returns201();

    void upsertByState_CoupleValidationsFailure_Returns400WithErrorList();

    void upsertByState_DifferentStateInBody_Returns400ConflictedData();

    void upsertByState_ExistWithDifferentComplytId_Returns400ConflictedData();

    void upsertByState_DoesntExistAndHasComplytId_Returns400ConflictedData();

    void upsertByState_ComplytIdFailedToParse_Returns400();

    void upsertByState_UnauthenticatedUser_Returns401();

    void upsertByState_UserWithoutAuthorities_Returns403();

    void upsertByState_UserWithoutCSRFToken_Returns403();

    void upsertByState_InternalServerError_Returns500();

    void upsertByState_NullHandler_ThrowsNullPointerException();
}

