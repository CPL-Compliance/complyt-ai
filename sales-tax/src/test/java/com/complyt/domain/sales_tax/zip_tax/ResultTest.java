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
        String expectedString = "Result(geoPostalCode=" + result.getGeoPostalCode() +
                ", geoCity=" + result.getGeoCity() +
                ", geoCounty=" + result.getGeoCounty() +
                ", geoState=" + result.getGeoState() +
                ", taxSales=" + result.getTaxSales() +
                ", taxUse=" + result.getTaxUse() +
                ", txbService=" + result.getTxbService() +
                ", txbFreight=" + result.getTxbFreight() +
                ", stateSalesTax=" + result.getStateSalesTax() +
                ", stateUseTax=" + result.getStateUseTax() +
                ", citySalesTax=" + result.getCitySalesTax() +
                ", cityUseTax=" + result.getCityUseTax() +
                ", cityTaxCode=" + result.getCityTaxCode() +
                ", countySalesTax=" + result.getCountySalesTax() +
                ", countyUseTax=" + result.getCountyUseTax() +
                ", countyTaxCode=" + result.getCityTaxCode() +
                ", districtSalesTax=" + result.getDistrictSalesTax() +
                ", districtUseTax=" + result.getDistrictUseTax() +
                ", district1Code=" + result.getDistrict1Code() +
                ", district1SalesTax=" + result.getDistrict1SalesTax() +
                ", district1UseTax=" + result.getDistrict1UseTax() +
                ", district2Code=" + result.getDistrict2Code() +
                ", district2SalesTax=" + result.getDistrict2SalesTax() +
                ", district2UseTax=" + result.getDistrict2UseTax() +
                ", district3Code=" + result.getDistrict3Code() +
                ", district3SalesTax=" + result.getDistrict3SalesTax() +
                ", district3UseTax=" + result.getDistrict3UseTax() +
                ", district4Code=" + result.getDistrict4Code() +
                ", district4SalesTax=" + result.getDistrict4SalesTax() +
                ", district4UseTax=" + result.getDistrict4UseTax() +
                ", district5Code=" + result.getDistrict5Code() +
                ", district5SalesTax=" + result.getDistrict5SalesTax() +
                ", district5UseTax=" + result.getDistrict5UseTax() +
                ", originDestination=" + result.getOriginDestination() + ")";

        // When
        String actualString = result.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void builder_Build_ReturnsResult() {
        // Given + When
        Result givenResult = Result.builder().taxSales(result.getTaxSales()).citySalesTax(result.getCitySalesTax())
                .countySalesTax(result.getCountySalesTax()).district1SalesTax(result.getDistrict1SalesTax()).districtSalesTax(result.getDistrictSalesTax())
                .stateSalesTax(result.getStateSalesTax()).cityTaxCode(result.getCityTaxCode()).cityUseTax(result.getCityUseTax())
                .countyTaxCode(result.getCountyTaxCode()).district1UseTax(result.getDistrict1UseTax()).district5SalesTax(result.getDistrict5SalesTax())
                .countyUseTax(result.getCountyUseTax()).district2SalesTax(result.getDistrict2SalesTax()).district2UseTax(result.getDistrict2UseTax())
                .district1Code(result.getDistrict1Code()).district2Code(result.getDistrict2Code()).district3Code(result.getDistrict3Code())
                .district3UseTax(result.getDistrict3UseTax()).district3SalesTax(result.getDistrict3SalesTax()).district4Code(result.getDistrict4Code())
                .district4SalesTax(result.getDistrict4SalesTax()).district4UseTax(result.getDistrict4UseTax()).district5Code(result.getDistrict5Code())
                .district5UseTax(result.getDistrict5UseTax()).districtUseTax(result.getDistrictUseTax()).geoCity(result.getGeoCity())
                .geoCounty(result.getGeoCounty()).geoState(result.getGeoState()).geoPostalCode(result.getGeoPostalCode())
                .txbFreight(result.getTxbFreight()).txbService(result.getTxbService()).originDestination(result.getOriginDestination()).build();

        // Then
        assertEquals(result, givenResult);
    }

}