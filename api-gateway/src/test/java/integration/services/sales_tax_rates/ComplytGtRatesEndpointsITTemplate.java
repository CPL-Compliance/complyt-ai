package integration.services.sales_tax_rates;

public interface ComplytGtRatesEndpointsITTemplate {
    void findByAddress_FindsGtAddressWithCountryAndRegion_ReturnsComplytGtRates();
    void findByAddress_FindsGtAddressWithOnlyCountry_ReturnsComplytGtRates();
}
