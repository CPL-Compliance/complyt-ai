package integration.services;

public interface SalesTaxRatesEndpointsITTemplate {

    void findByAddress_FirstAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt();

    void findByAddress_SecondAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt();
}
