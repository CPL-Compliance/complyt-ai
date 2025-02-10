package integration.services.sales_tax_rates;

public interface ComplytSalesTaxRatesEndpointsITTemplate {
    // --- GET request ---
    // CachedAddress
    void findByAddress_CachedAddressByQuery_InternalRateByMaxDate_Returns200();
    void findByAddress_CachedAddressByQuery_InternalRateBeforeMaxDate_Returns200();
    void findByAddress_CachedAddress_FastTax_Returns200();
}
