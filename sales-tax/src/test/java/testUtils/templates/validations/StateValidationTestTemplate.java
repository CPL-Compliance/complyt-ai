package testUtils.templates.validations;

public interface StateValidationTestTemplate {
    void upsert_NullState_Returns400ValidationError();

    void upsert_BlankAbbreviationInState_Returns400ValidationError();

    void upsert_BlankCodeInState_Returns400ValidationError();

    void upsert_BlankNameInState_Returns400ValidationError();

    void upsert_LengthOf257AbbreviationInState_Returns400ValidationError();

    void upsert_LengthOf257CodeInState_Returns400ValidationError();

    void upsert_LengthOf257NameInState_Returns400ValidationError();

    void upsert_NullAbbreviationInState_Returns400ValidationError();

    void upsert_NullCodeInState_Returns400ValidationError();

    void upsert_NullNameInState_Returns400ValidationError();
}
