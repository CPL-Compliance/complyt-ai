package test_utils.ut.templates.validations;

public interface AddressValidationTestTemplate {

    // Nulls
    void get_NullCity_Returns400FailedValidation();
    void get_NullCountry_Returns400FailedValidation();
    void get_NullState_Returns400FailedValidation();
    void get_NullStreet_Returns400FailedValidation();
    void get_NullZip_Returns400FailedValidation();
    
    // Blanks
    void get_BlankZip_Returns400FailedValidation();
    void get_BlankCountry_Returns400FailedValidation();
    void get_BlankStreet_Returns400FailedValidation();
    void get_BlankCity_Returns400FailedValidation();
    void get_BlankState_Returns400FailedValidation();

    // Max Length
    void get_ZipMoreThan20Char_Returns400FailedValidation();
    void get_CountryMoreThan50Char_Returns400FailedValidation();
    void get_StreetMoreThan200Char_Returns400FailedValidation();
    void get_StateMoreThan100Char_Returns400FailedValidation();
    void get_CityMoreThan100Char_Returns400FailedValidation();
    
    void get_partialAddressWithMinimumParams_Returns200();

}
