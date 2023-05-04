package com.example.complyt.domain.zip_tax;

import com.complyt.domain.zip_tax.Result;
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
        String expectedString = "Result[geoPostalCode=, geoCity=, geoCounty=injectedCounty, geoState=, taxSales=0.0, taxUse=0.0, txbService=, txbFreight=, stateSalesTax=0.0, stateUseTax=0.0, citySalesTax=0.0, cityUseTax=0.0, cityTaxCode=, countySalesTax=0.0, countyUseTax=0, countyTaxCode=, districtSalesTax=0.0, districtUseTax=0.0, district1Code=, district1SalesTax=0.0, district1UseTax=0.0, district2Code=, district2SalesTax=0, district2UseTax=0, district3Code=, district3SalesTax=0, district3UseTax=0, district4Code=, district4SalesTax=0.0, district4UseTax=0.0, district5Code=, district5SalesTax=0, district5UseTax=0, originDestination=]";

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
    void toString_ReturnString() {
        // Given
        String expectedString = "Result[geoPostalCode=" + result.geoPostalCode() +
                ", geoCity=" + result.geoCity() +
                ", geoCounty=" + result.geoCounty() +
                ", geoState=" + result.geoState() +
                ", taxSales=" + result.taxSales() +
                ", taxUse=" + result.taxUse() +
                ", txbService=" + result.txbService() +
                ", txbFreight=" + result.txbFreight() +
                ", stateSalesTax=" + result.stateSalesTax() +
                ", stateUseTax=" + result.stateUseTax() +
                ", citySalesTax=" + result.citySalesTax() +
                ", cityUseTax=" + result.cityUseTax() +
                ", cityTaxCode=" + result.cityTaxCode() +
                ", countySalesTax=" + result.countySalesTax() +
                ", countyUseTax=" + result.countyUseTax() +
                ", countyTaxCode=" + result.cityTaxCode() +
                ", districtSalesTax=" + result.districtSalesTax() +
                ", districtUseTax=" + result.districtUseTax() +
                ", district1Code=" + result.district1Code() +
                ", district1SalesTax=" + result.district1SalesTax() +
                ", district1UseTax=" + result.district1UseTax() +
                ", district2Code=" + result.district2Code() +
                ", district2SalesTax=" + result.district2SalesTax() +
                ", district2UseTax=" + result.district2UseTax() +
                ", district3Code=" + result.district3Code() +
                ", district3SalesTax=" + result.district3SalesTax() +
                ", district3UseTax=" + result.district3UseTax() +
                ", district4Code=" + result.district4Code() +
                ", district4SalesTax=" + result.district4SalesTax() +
                ", district4UseTax=" + result.district4UseTax() +
                ", district5Code=" + result.district5Code() +
                ", district5SalesTax=" + result.district5SalesTax() +
                ", district5UseTax=" + result.district5UseTax() +
                ", originDestination=" + result.originDestination() + "]";

        // When
        String actualString = result.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void builder_Build_ReturnsResult() {
        // Given + When
        Result givenResult = Result.builder().taxSales(result.taxSales()).citySalesTax(result.citySalesTax())
                .countySalesTax(result.countySalesTax()).district1SalesTax(result.district1SalesTax()).districtSalesTax(result.districtSalesTax())
                .stateSalesTax(result.stateSalesTax()).cityTaxCode(result.cityTaxCode()).cityUseTax(result.cityUseTax())
                .countyTaxCode(result.countyTaxCode()).district1UseTax(result.district1UseTax()).district5SalesTax(result.district5SalesTax())
                .countyUseTax(result.countyUseTax()).district2SalesTax(result.district2SalesTax()).district2UseTax(result.district2UseTax())
                .district1Code(result.district1Code()).district2Code(result.district2Code()).district3Code(result.district3Code())
                .district3UseTax(result.district3UseTax()).district3SalesTax(result.district3SalesTax()).district4Code(result.district4Code())
                .district4SalesTax(result.district4SalesTax()).district4UseTax(result.district4UseTax()).district5Code(result.district5Code())
                .district5UseTax(result.district5UseTax()).districtUseTax(result.districtUseTax()).geoCity(result.geoCity())
                .geoCounty(result.geoCounty()).geoState(result.geoState()).geoPostalCode(result.geoPostalCode())
                .txbFreight(result.txbFreight()).txbService(result.txbService()).originDestination(result.originDestination()).build();

        // Then
        assertEquals(result, givenResult);
    }

}