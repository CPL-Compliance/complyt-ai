package testUtils.templates.validations;

public interface InternalTimestampsValidationTestTemplate {

    void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError();

    void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_BlankTimestampInUpdatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_BlankTimestampInCreatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_29OfFebruaryNotInLeapYearInCreatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_29OfFebruaryNotInLeapYearInUpdatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_9DigitsAfterTheDotInSecondsInCreatedDateInInternalTimestamp_Returns200Ok();

    void upsert_9DigitsAfterTheDotInSecondsInUpdatedDateInInternalTimestamp_Returns200Ok();

    void upsert_10DigitsAfterTheDotInSecondsInCreatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_10DigitsAfterTheDotInSecondsInUpdatedDateInInternalTimestamp_Returns400ValidationError();

    void upsert_ZoneSetWithOffsetOfZInCreatedDateInInternalTimestamp_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfZInUpdatedDateInInternalTimestamp_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfPlusTimeInCreatedDateInInternalTimestamp_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfPlusTimeInUpdatedDateInInternalTimestamp_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfMinusTimeInCreatedDateInInternalTimestamp_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfMinusTimeInUpdatedDateInInternalTimestamp_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfMoreThan18InCreatedDateInInternalTimestamps_Returns400ValidationError();

    void upsert_ZoneSetWithOffsetOfMoreThan18InUpdatedDateInInternalTimestamps_Returns400ValidationError();

    void upsert_JustDateWithNoTimeOffsetInUpdatedDateInInternalTimestamps_Returns200Ok();

    void upsert_JustDateWithNoTimeOffsetInCreatedDateInInternalTimestamps_Returns200Ok();
}
