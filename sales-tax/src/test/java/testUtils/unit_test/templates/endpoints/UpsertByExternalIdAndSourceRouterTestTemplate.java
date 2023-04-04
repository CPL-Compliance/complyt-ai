package testUtils.unit_test.templates.endpoints;

public interface UpsertByExternalIdAndSourceRouterTestTemplate {
    void upsertByExternalIdAndSource_Exists_Returns200();

    void upsertByExternalIdAndSource_DoesntExists_Returns201();

    void upsertByExternalIdAndSource_CoupleValidationsFailure_Returns400WithErrorList();

    void upsertByExternalIdAndSource_DifferentSourceInBody_Returns400ConflictedData();

    void upsertByExternalIdAndSource_DifferentExternalIdInBody_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ExistWithDifferentComplytId_Returns400ConflictedData();

    void upsertByExternalIdAndSource_DoesntExistAndHasComplytId_Returns400ConflictedData();

    void upsertByExternalIdAndSource_BlankSource_Returns400ValidationError();

    void upsertByExternalIdAndSource_nonDigitSource_Returns400ValidationError();

    void upsertByExternalIdAndSource_MoreThenOneDigitSource_Returns400ValidationError();

    void upsertByExternalIdAndSource_BlankExternalId_Returns400ValidationError();

    void upsertByExternalIdAndSource_LengthGreaterThen256ExternalId_Returns400ValidationError();

    void upsertByExternalIdAndSource_ComplytIdFailedToParse_Returns400();

    void upsertByExternalIdAndSource_UnauthenticatedUser_Returns401();

    void upsertByExternalIdAndSource_UserWithoutAuthorities_Returns403();

    void upsertByExternalIdAndSource_UserWithoutCSRFToken_Returns403();

    void upsertByExternalIdAndSource_InternalServerError_Returns500();

    void upsertByExternalIdAndSource_NullHandler_ThrowsNullPointerException();
}

