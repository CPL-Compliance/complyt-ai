package integration.test_utils.templates.endpoints;

import integration.test_utils.templates.methods.PutITTemplate;

public interface UpsertByStateITTemplate extends PutITTemplate {

    void upsertByState_Exists_Returns200();

    void upsertByState_DoesntExists_Returns201();

    void upsertByState_DoesntExistsWithComplytId_Returns400ConflictedData();

    void upsertByState_ConflictingState_Returns400ConflictedData();

    void upsertByState_DoesntPassValidation_Returns400CValidationError();

    void upsertByState_NoBody_Returns400();
}
