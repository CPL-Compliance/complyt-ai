package testUtils.unit_test.templates.endpoints;

public interface UpsertByTenantIdRouterTestTemplate {
    void upsertByTenantId_Exists_Returns200();

    void upsertByTenantId_PathVariableInvalid_Returns400();

    void upsertByTenantId_BlankNexus_Returns400ValidationError();

    void upsertByTenantId_BlankName_Returns400ValidationError();

    void upsertByTenantId_BlankTenantId_Returns400ValidationError();

    void upsertByTenantId_LengthGreaterThen256Name_Returns400ValidationError();

    void upsertByTenantId_DifferentTenantIdInBody_Returns400ConflictedData();

    void upsertByTenantId_DoesntExists_Returns201();

    void upsertByTenantId_UnauthenticatedUser_Returns401();

    void upsertByTenantId_InternalServerError_Returns500();

    void upsertByTenantId_NullHandler_ThrowsNullPointerException();

    void upsertByTenantId_UnsupportedMediaType_Returns415();
}
