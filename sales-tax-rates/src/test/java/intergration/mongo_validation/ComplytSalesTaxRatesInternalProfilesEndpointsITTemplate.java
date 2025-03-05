package intergration.mongo_validation;

public interface ComplytSalesTaxRatesInternalProfilesEndpointsITTemplate {
    // Address Found & Rate Found
    void findAddress_InternalRateFound_DateBeforeMaxEffectiveDate_Return200();
    void findAddress_InternalRateFound_DateAfterMaxEffectiveDate_Return200();

    // Internal Not Found & ExternalFound
    void findAddress_InternalRateNotFound_ExternalRateFoundInDB_Return200();
    void findAddress_InternalRateNotFound_ExternalRateClientWrapper_Return200();

    // Put Internal Rate
    void update_newInternalRate_Return200();
    void update_InsertNewInternalRate_StatusWrong_Return400();
    void update_ArchiveInternalRate_Return200();
    void update_updateInternalRate_Return200();
    void update_updateInternalRate_NotFound_Return404();
}
