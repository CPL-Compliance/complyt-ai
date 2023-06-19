package integration.services;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public interface SalesTaxRatesEndpointsITTemplate {

    void findByAddress_FirstAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt();

    @Order(2)
    @Test
    void findByAddress_SecondAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt();
}
