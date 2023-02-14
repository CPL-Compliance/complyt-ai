package testUtils.templates.validations;

public interface ExternalTimestampsValidationRouterTest {
    void upsert_NullExternalTimestamps_Returns400ValidationError();

    void upsert_NullCreatedDateInExternalTimestamps_Returns400ValidationError();

    void upsert_NullUpdatedDateInExternalTimestamps_Returns400ValidationError();

    void upsert_InvalidTimestampInUpdatedDateInExternalTimestamps_Returns400ValidationError();

    void upsert_InvalidTimestampInCreatedDateInExternalTimestamps_Returns400ValidationError();

}
