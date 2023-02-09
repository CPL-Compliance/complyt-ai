package testUtils.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface UpsertByExternalIdAndSourceRouterTest {
    @Test
    void upsertByExternalIdAndSource_Exists_Returns200();

    @Test
    void upsertByExternalIdAndSource_DoesntExists_Returns201();

    @Test
    void upsertByExternalIdAndSource_CoupleValidationsFailure_Returns400WithErrorList();

    @Test
    void upsertByExternalIdAndSource_DifferentSourceInBody_Returns400ConflictedData();

    @Test
    void upsertByExternalIdAndSource_DifferentExternalIdInBody_Returns400ConflictedData();

    @Test
    void upsertByExternalIdAndSource_ExistWithDifferentComplytId_Returns400ConflictedData();

    @Test
    void upsertByExternalIdAndSource_DoesntExistAndHasComplytId_Returns400ConflictedData();

    @Test
    void upsertByExternalIdAndSource_BlankSource_Returns400ValidationError();

    @Test
    void upsertByExternalIdAndSource_nonDigitSource_Returns400ValidationError();

    @Test
    void upsertByExternalIdAndSource_MoreThenOneDigitSource_Returns400ValidationError();

    @Test
    void upsertByExternalIdAndSource_BlankExternalId_Returns400ValidationError();

    @Test
    void upsertByExternalIdAndSource_LengthGreaterThen256ExternalId_Returns400ValidationError();

    @Test
    void upsertByExternalIdAndSource_ComplytIdFailedToParse_Returns400();

    @Test
    void upsertByExternalIdAndSource_UnauthenticatedUser_Returns401();

    @Test
    void upsertByExternalIdAndSource_UserWithoutAuthorities_Returns403();

    @Test
    void upsertByExternalIdAndSource_UserWithoutCSRFToken_Returns403();

    @Test
    void upsertByExternalIdAndSource_InternalServerError_Returns500();

    @Test
    void upsertByExternalIdAndSource_NullHandler_ThrowsNullPointerException();
}

