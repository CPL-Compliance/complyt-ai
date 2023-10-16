package integration.services.sales_tax_rates;

public interface SalesTaxRatesEndpointsITTemplate {

    void findByAddress_FirstAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt();

    void findByAddress_SecondAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt();
}
