package testUtils.integration_test.templates.endpoints;

public interface UpsertByExternalIdAndSourceITTemplate {

    void upsertByExternalIdAndSource_Exists_Returns200();

    void upsertByExternalIdAndSource_DoesntExists_Returns201();

    void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingExternalId_Returns400ConflictedData();

    void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError();
}
