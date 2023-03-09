package testUtils.templates.validations;

public interface ValidationDatesValidationTestTemplate {

    void upsert_NullFromDateInValidationDates_Returns400ValidationError();

    void upsert_NullToDateInValidationDates_Returns400ValidationError();

    void upsert_BlankTimestampInToDateInValidationDates_Returns400ValidationError();

    void upsert_BlankTimestampInFromDateInValidationDates_Returns400ValidationError();

    void upsert_29OfFebruaryNotInLeapYearInFromDateInValidationDates_Returns400ValidationError();

    void upsert_29OfFebruaryNotInLeapYearInToDateInValidationDates_Returns400ValidationError();

    void upsert_9DigitsAfterTheDotInSecondsInFromDateInValidationDates_Returns200Ok();

    void upsert_9DigitsAfterTheDotInSecondsInToDateInValidationDates_Returns200Ok();

    void upsert_10DigitsAfterTheDotInSecondsInFromDateInValidationDates_Returns400ValidationError();

    void upsert_10DigitsAfterTheDotInSecondsInToDateInValidationDates_Returns400ValidationError();

    void upsert_ZoneSetWithOffsetOfZInFromDateInValidationDates_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfZInToDateInValidationDates_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfPlusTimeInFromDateInValidationDates_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfPlusTimeInToDateInValidationDates_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfMinusTimeInFromDateInValidationDates_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfMinusTimeInToDateInValidationDates_Returns200Ok();

    void upsert_ZoneSetWithOffsetOfMoreThan18InFromDateInValidationDates_Returns400ValidationError();

    void upsert_ZoneSetWithOffsetOfMoreThan18InToDateInValidationDates_Returns400ValidationError();

    void upsert_JustDateWithNoTimeOffsetToDateInValidationDates_Returns200Ok();

    void upsert_JustDateWithNoTimeOffsetFromDateInValidationDates_Returns200Ok();

}
