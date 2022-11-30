package com.complyt.domain.sales_tax.zip_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResultTest {

    Result result;

    @BeforeEach
    void setUp() {
        result = createResult();

        assertNotNull(result);
    }

    @Test
    void testToString() {
        String resultStr = "Result(geoPostalCode=, geoCity=, geoCounty=injectedCounty, geoState=, taxSales=0.0, taxUse=0.0, txbService=, txbFreight=, stateSalesTax=0.0, stateUseTax=0.0, citySalesTax=0.0, cityUseTax=0.0, cityTaxCode=, countySalesTax=0.0, countyUseTax=0, countyTaxCode=, districtSalesTax=0.0, districtUseTax=0.0, district1Code=, district1SalesTax=0.0, district1UseTax=0.0, district2Code=, district2SalesTax=0, district2UseTax=0, district3Code=, district3SalesTax=0, district3UseTax=0, district4Code=, district4SalesTax=0.0, district4UseTax=0.0, district5Code=, district5SalesTax=0, district5UseTax=0, originDestination=)";

        assertEquals(resultStr, result.toString());
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        // Given + When
        Result anotherResult = createResult();

        // Then
        assertEquals(result, anotherResult);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        // Given + When
        Result anotherResult = createResult();

        // Then
        assertEquals(result.hashCode(), anotherResult.hashCode());
    }

    @Test
    void noArgsConstructor_ReturnEmptyResult() {
        // Given + When
        Result givenResult = new Result();

        // Then
        assertEquals("Result(geoPostalCode=null, geoCity=null, geoCounty=null, geoState=null, taxSales=0.0, taxUse=0.0, txbService=null, txbFreight=null, stateSalesTax=0.0, stateUseTax=0.0, citySalesTax=0.0, cityUseTax=0.0, cityTaxCode=null, countySalesTax=0.0, countyUseTax=0, countyTaxCode=null, districtSalesTax=0.0, districtUseTax=0.0, district1Code=null, district1SalesTax=0.0, district1UseTax=0.0, district2Code=null, district2SalesTax=0, district2UseTax=0, district3Code=null, district3SalesTax=0, district3UseTax=0, district4Code=null, district4SalesTax=0.0, district4UseTax=0.0, district5Code=null, district5SalesTax=0, district5UseTax=0, originDestination=null)", givenResult.toString());
    }

    @Test
    void builder_Build_ReturnResult() {
        // Given + When
        Result givenResult = Result.builder().taxSales(0).citySalesTax(0)
                .countySalesTax(0).district1SalesTax(0).districtSalesTax(0)
                .stateSalesTax(0).cityTaxCode("").cityUseTax(0)
                .countyTaxCode("").district1UseTax(0).district5SalesTax(0)
                .countyUseTax(0).district2SalesTax(0).district2UseTax(0)
                .district1Code("").district2Code("").district3Code("")
                .district3UseTax(0).district3SalesTax(0).district4Code("")
                .district4SalesTax(0).district4UseTax(0).district5Code("")
                .district5UseTax(0).districtUseTax(0).geoCity("")
                .geoCounty("injectedCounty").geoState("").geoPostalCode("")
                .txbFreight("").txbService("").originDestination("").build();

        // Then
        assertEquals(result, givenResult);
    }

    private Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }
}