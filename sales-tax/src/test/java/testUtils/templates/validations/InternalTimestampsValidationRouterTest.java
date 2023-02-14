package testUtils.templates.validations;

import org.junit.jupiter.api.Test;

public interface InternalTimestampsValidationRouterTest {

    void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError();

    void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_InvalidTimestampInUpdatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_InvalidTimestampInCreatedDateInInternalTimestamp_Returns400ValidationError();
}
