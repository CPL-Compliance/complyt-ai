package integration.services;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public interface SalesTaxRatesEndpointsITTemplate {

    void findByAddress_FirstAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt();
    
    void findByAddress_SecondAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt();
}
