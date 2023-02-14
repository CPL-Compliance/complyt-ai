package testUtils.templates.validations;

public interface ValidationDatesValidationRouterTest {

    void upsert_NullFromDateInValidationDates_Returns400ValidationError();

    void upsert_NullToDateInValidationDates_Returns400ValidationError();

    void upsert_BlankTimestampInToDateInValidationDates_Returns400ValidationError();

    void upsert_BlankTimestampInFromDateInValidationDates_Returns400ValidationError();

}
