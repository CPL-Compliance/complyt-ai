package com.complyt.v1.routers;

public interface VatValidationRouterTestTemplate {
    // cases
    void upsert_ValidateNewVatDetails_Return201();

    void upsert_ValidateAlreadyExistingVatDetails_Return200();

    void upsert_ValidateVatDetailsNotValid_Return400CountryNotValid();

    void upsert_VatDetailsCouldNotBeValidatedDueToInternalError_Return500();

    // input validation
    void upsert_CountryCodeIsBlank_Return400();

    void upsert_CountryCodeIsNull_Return400();

    void upsert_CountryCodeIsMoreThan50Characters_Return400();

    void upsert_VatNumberIsBlank_Return400();

    void upsert_VatNumberIsNull_Return400();

    void upsert_VatNumberIsMoreThan20Characters_Return400();

    void upsert_NullHandler_ThrowsNullPointerException();
}
