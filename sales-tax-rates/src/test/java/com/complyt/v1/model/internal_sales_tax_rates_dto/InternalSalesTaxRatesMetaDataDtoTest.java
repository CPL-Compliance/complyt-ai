package com.complyt.v1.model.internal_sales_tax_rates_dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InternalSalesTaxRatesMetaDataDtoTest {

    private final InternalSalesTaxRatesMetaDataDto internalSalesTaxRatesMetaDataDto = new InternalSalesTaxRatesMetaDataDto(
            "MTA",
            "SPD",
            "Other1",
            "Other2",
            "Other3",
            "Other4",
            "12345",
            "67890",
            "11111",
            "22222",
            "33333",
            "44444",
            "06",
            "037",
            "44000",
            "0603744000",
            "100000",
            "2%",
            "50000",
            "1%",
            "Yes",
            "No"
    );

    @Test
    public void testToString() {
        String expectedString = "InternalSalesTaxRatesMetaDataDto(" +
                "mtaName=MTA, spdName=SPD, other1Name=Other1, other2Name=Other2, other3Name=Other3, other4Name=Other4, " +
                "mtaNumber=12345, spdNumber=67890, other1Number=11111, other2Number=22222, other3Number=33333, other4Number=44444, " +
                "fipsState=06, fipsCounty=037, fipsCity=44000, fipsGeocode=0603744000, " +
                "countyTaxableMax=100000, countyTaxOverMax=2%, cityTaxableMax=50000, cityTaxOverMax=1%, " +
                "taxShippingAlone=Yes, taxShippingAndHandlingTogether=No)";

        assertEquals(expectedString, internalSalesTaxRatesMetaDataDto.toString());
    }
}