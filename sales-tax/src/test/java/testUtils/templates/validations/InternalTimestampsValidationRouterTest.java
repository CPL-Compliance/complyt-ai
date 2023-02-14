package testUtils.templates.validations;

public interface InternalTimestampsValidationRouterTest {

    void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError();

    void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_BlankTimestampInUpdatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_BlankTimestampInCreatedDateInInternalTimestamp_Returns400ValidationError();
}
