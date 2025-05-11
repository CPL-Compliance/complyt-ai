package integration.services.sales_tax_rates;

public interface ComplytSalesTaxRatesEndpointsITTemplate {
    // --- GET request ---
    // CachedAddress
    void findByAddress_CachedAddressByQuery_InternalRateByMaxDate_Returns200();
    void findByAddress_CachedAddressByQuery_InternalRateBeforeMaxDate_Returns200();
    void findByAddress_CachedAddress_FastTax_Returns200();
    void findByAddress_CachedAddressByQueryDetailedTrue_Returns200();
    // --- PUT request ---
    void update_newInternalRate_Return200();
    void update_InsertNewInternalRate_StatusWrong_Return400();
    void update_ArchiveInternalRate_Return200();
    void update_UpdateInternalRate_Return200();
    void update_UpdateInternalRate_NotFound_Return404();
    void update_ArchiveInternalRate_NotFound_Return404();
}
