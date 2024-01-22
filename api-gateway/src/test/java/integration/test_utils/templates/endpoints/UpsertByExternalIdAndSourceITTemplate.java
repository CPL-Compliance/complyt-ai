package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.PutITTemplate;

public interface UpsertByExternalIdAndSourceITTemplate extends PutITTemplate {

    void upsertByExternalIdAndSource_Exists_Returns200();

    void upsertByExternalIdAndSource_PathVariableInvalid_Returns400();

    void upsertByExternalIdAndSource_DoesntExists_Returns201();

    void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingExternalId_Returns400ConflictedData();

    void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError();

    void upsertByExternalIdAndSource_NoBody_Returns400();
}
