package intergration.mongo_validation;

public interface ComplytSalesTaxRatesInternalProfilesEndpointsITTemplate {
    // Address Found & Rate Found
    void findAddress_InternalRateFound_DateBeforeMaxEffectiveDate_Return200();
    void findAddress_InternalRateFound_DateAfterMaxEffectiveDate_Return200();

    // Internal Not Found & ExternalFound
    void findAddress_InternalRateNotFound_ExternalRateFoundInDB_Return200();
    void findAddress_InternalRateNotFound_ExternalRateClientWrapper_Return200();
}
