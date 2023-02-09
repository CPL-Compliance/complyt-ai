package testUtils.templates.validations;

import org.junit.jupiter.api.Test;

public interface ExternalTimestampsValidationRouterTest {
    @Test
    void upsert_NullExternalTimestamp_Returns400ValidationError();

    @Test
    void upsert_NullCreatedDateInExternalTimestamps_Returns400ValidationError();

    @Test
    void upsert_NullUpdatedDateInExternalTimestamp_Returns400ValidationError();

    @Test
    void upsert_NullTimestampInCreatedDateInExternalTimestamps_Returns400ValidationError();

    @Test
    void upsert_NullTimestampInUpdatedDateInExternalTimestamp_Returns400ValidationError();
}
