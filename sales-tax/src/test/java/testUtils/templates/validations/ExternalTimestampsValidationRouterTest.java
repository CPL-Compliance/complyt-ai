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
    void upsert_InvalidTimestampInUpdatedDateInExternalTimestamp_Returns400ValidationError();

    @Test
    void upsert_InvalidTimestampInCreatedDateInExternalTimestamp_Returns400ValidationError();

}
