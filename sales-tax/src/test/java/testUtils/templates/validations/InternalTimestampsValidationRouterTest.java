package testUtils.templates.validations;

import org.junit.jupiter.api.Test;

public interface InternalTimestampsValidationRouterTest {
    @Test
    void upsert_NullInternalTimestamp_Returns400ValidationError();

    @Test
    void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError();

    @Test
    void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError();

    @Test
    void upsert_NullTimestampInCreatedDateInInternalTimestamps_Returns400ValidationError();

    @Test
    void upsert_NullTimestampInUpdatedDateInInternalTimestamp_Returns400ValidationError();
}
