package testUtils.integration_test.templates.endpoints;

public interface UpsertByStateITTemplate {

    void upsertByState_Exists_Returns200();

    void upsertByState_DoesntExists_Returns201();

    void upsertByState_DoesntExistsWithComplytId_Returns400ConflictedData();

    void upsertByState_ConflictingState_Returns400ConflictedData();

    void upsertByState_DoesntPassValidation_Returns400CValidationError();

    void upsertByState_NoBody_Returns400();
}
