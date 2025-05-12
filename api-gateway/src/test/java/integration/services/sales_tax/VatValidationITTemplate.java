package integration.services.sales_tax;

public interface VatValidationITTemplate {
    // cases
    void upsert_ValidateExitingVatDetails_Return200();
    void upsert_ValidateExitingVatDetailsCountryCodeIsFullCountryNameAndCodeExistsInVatNumber_Return200();
    void upsert_ValidateNewVatDetails_Return201();
    void upsert_ValidateAlreadyExistingVatDetails_Return200();
    void upsert_ValidateVatDetailsNotValid_Return201WithSameValuesAsInputAndValidFalse();

    // input validation
    void upsert_CountryCodeIsBlank_Return400();
    void upsert_CountryCodeIsNull_Return400();
    void upsert_CountryCodeIsMoreThan50Characters_Return400();
    void upsert_VatNumberIsBlank_Return400();
    void upsert_VatNumberIsNull_Return400();
    void upsert_VatNumberIsMoreThan20Characters_Return400();

    void upsert_ErrorInValidationWebClient_Return400BadCountry();
}
