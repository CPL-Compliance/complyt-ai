package testUtils.templates.validations;

public interface ExternalTimestampsValidationTestTemplate {
    void upsert_NullExternalTimestamps_Returns400ValidationError();

    void upsert_NullCreatedDateInExternalTimestamps_Returns400ValidationError();

    void upsert_NullUpdatedDateInExternalTimestamps_Returns400ValidationError();

    void upsert_BlankTimestampInUpdatedDateInExternalTimestamps_Returns400ValidationError();

    void upsert_BlankTimestampInCreatedDateInExternalTimestamps_Returns400ValidationError();

    void upsert_29OfFebruaryNotInLeapYearInCreatedDateInExternalTimestamps_Returns400ValidationError();
    void upsert_29OfFebruaryNotInLeapYearInUpdatedDateInExternalTimestamps_Returns400ValidationError();
    void upsert_9DigitsAfterTheDotInSecondsInCreatedDateInExternalTimestamps_Returns200Ok();
    void upsert_9DigitsAfterTheDotInSecondsInUpdatedDateInExternalTimestamps_Returns200Ok();
    void upsert_10DigitsAfterTheDotInSecondsInCreatedDateInExternalTimestamps_Returns400ValidationError();
    void upsert_10DigitsAfterTheDotInSecondsInUpdatedDateInExternalTimestamps_Returns400ValidationError();
    void upsert_ZoneSetWithOffsetOfZInCreatedDateInExternalTimestamps_Returns200Ok();
    void upsert_ZoneSetWithOffsetOfZInUpdatedDateInExternalTimestamps_Returns200Ok();
    void upsert_ZoneSetWithOffsetOfPlusTimeInCreatedDateInExternalTimestamps_Returns200Ok();
    void upsert_ZoneSetWithOffsetOfPlusTimeInUpdatedDateInExternalTimestamps_Returns200Ok();
    void upsert_ZoneSetWithOffsetOfMinusTimeInCreatedDateInExternalTimestamps_Returns200Ok();
    void upsert_ZoneSetWithOffsetOfMinusTimeInUpdatedDateInExternalTimestamps_Returns200Ok();
    void upsert_ZoneSetWithOffsetOfMoreThan18InCreatedDateInExternalTimestamps_Returns400ValidationError();
    void upsert_ZoneSetWithOffsetOfMoreThan18InUpdatedDateInExternalTimestamps_Returns400ValidationError();
    void upsert_JustDateWithNoTimeOffsetInUpdatedDateInExternalTimestamps_Returns200Ok();
    void upsert_JustDateWithNoTimeOffsetInCreatedDateInExternalTimestamps_Returns200Ok();


}
