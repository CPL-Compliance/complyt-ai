package testUtils.templates.validations;

public interface ValidationDatesValidationRouterTest {
    void upsert_NullValidationDates_Returns400ValidationError();

    void upsert_NullCreatedDateInValidationDates_Returns400ValidationError();

    void upsert_NullUpdatedDateInValidationDates_Returns400ValidationError();

    void upsert_InvalidTimestampInUpdatedDateInValidationDates_Returns400ValidationError();

    void upsert_InvalidTimestampInCreatedDateInValidationDates_Returns400ValidationError();

}
