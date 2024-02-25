package testUtils.integration_test.templates.endpoints;

public interface UpsertByTenantIdITTemplate {
    void upsertByTenantId_Exists_Returns200();
    void upsertByTenantId_PathVariableInvalid_Returns400();
    void upsertByTenantId_ConflictedTenantId_Returns400();
    void upsertByTenantId_DoesntExists_Returns201();
    void upsertByTenantId_UnsupportedMediaType_Returns415();
}
