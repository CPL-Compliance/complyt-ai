package com.complyt.domain.sales_tax.zip_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    Result result;

    @BeforeEach
    void setUp() {
        result = createResult();

        assertNotNull(result);
    }

    private Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "Result(geoPostalCode=, geoCity=, geoCounty=injectedCounty, geoState=, taxSales=0.0, taxUse=0.0, txbService=, txbFreight=, stateSalesTax=0.0, stateUseTax=0.0, citySalesTax=0.0, cityUseTax=0.0, cityTaxCode=, countySalesTax=0.0, countyUseTax=0, countyTaxCode=, districtSalesTax=0.0, districtUseTax=0.0, district1Code=, district1SalesTax=0.0, district1UseTax=0.0, district2Code=, district2SalesTax=0, district2UseTax=0, district3Code=, district3SalesTax=0, district3UseTax=0, district4Code=, district4SalesTax=0.0, district4UseTax=0.0, district5Code=, district5SalesTax=0, district5UseTax=0, originDestination=)";

        // When
        String actualString = result.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void equals_IdenticalCustomers_ReturnsTrue() {
        // Given
        Result anotherResult = createResult();

        // When
        boolean isEquals = result.equals(anotherResult);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        // Given + When
        Result anotherResult = createResult();

        // Then
        assertEquals(result.hashCode(), anotherResult.hashCode());
    }

    @Test
    void noArgsConstructor_ReturnsEmptyResult() {
        // Given
        String expectedString = "Result(geoPostalCode=, geoCity=, geoCounty=injectedCounty" +
                ", geoState=, taxSales=0.0, taxUse=0.0, txbService=" +
                ", txbFreight=, stateSalesTax=0.0, stateUseTax=0.0" +
                ", citySalesTax=0.0, cityUseTax=0.0, cityTaxCode=" +
                ", countySalesTax=0.0, countyUseTax=0, countyTaxCode=" +
                ", districtSalesTax=0.0, districtUseTax=0.0, district1Code=" +
                ", district1SalesTax=0.0, district1UseTax=0.0, district2Code=" +
                ", district2SalesTax=0, district2UseTax=0, district3Code=" +
                ", district3SalesTax=0, district3UseTax=0, district4Code=" +
                ", district4SalesTax=0.0, district4UseTax=0.0, district5Code=" +
                ", district5SalesTax=0, district5UseTax=0, originDestination=)";

        // When
        String actualString = result.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void builder_Build_ReturnsResult() {
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

}