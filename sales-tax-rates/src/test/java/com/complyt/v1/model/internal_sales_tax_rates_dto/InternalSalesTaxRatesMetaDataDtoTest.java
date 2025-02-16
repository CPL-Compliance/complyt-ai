package com.complyt.v1.model.internal_sales_tax_rates_dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InternalSalesTaxRatesMetaDataDtoTest {

    private final InternalSalesTaxRatesMetaDataDto internalSalesTaxRatesMetaDataDto = new InternalSalesTaxRatesMetaDataDto(
            "MTA",                        // recordType
            "SPD",                        // stateAbbrev
            "Other1",                     // stateUseTax
            "Other2",                     // countyUseTax
            "Other3",                     // cityUseTax
            "mtaUseTax",                  // mtaUseTax (new field, value identical to name)
            "spdUseTax",                  // spdUseTax (new field, value identical to name)
            "other1UseTax",               // other1UseTax (new field, value identical to name)
            "other2UseTax",               // other2UseTax (new field, value identical to name)
            "other3UseTax",               // other3UseTax (new field, value identical to name)
            "other4UseTax",               // other4UseTax (new field, value identical to name)
            "44444",                      // totalUseTax
            "06",                         // countyRptCode
            "037",                        // cityRptCode
            "mtaName",                    // mtaName (new field, value identical to name)
            "0603744000",                 // mtaNumber
            "spdName",                    // spdName (new field, value identical to name)
            "100000",                     // spdNumber
            "other1Name",                 // other1Name (new field, value identical to name)
            "2%",                         // other1Number
            "other2Name",                 // other2Name (new field, value identical to name)
            "50000",                      // other2Number
            "other3Name",                 // other3Name (new field, value identical to name)
            "1%",                         // other3Number
            "other4Name",                 // other4Name (new field, value identical to name)
            "Yes",                        // other4Number
            "taxShippingAlone",           // taxShippingAlone (new field, value identical to name)
            "taxShippingAndHandlingTogether", // taxShippingAndHandlingTogether (new field, value identical to name)
            "06",                         // fipsState
            "037",                        // fipsCounty
            "44000",                      // fipsCity
            "0603744000",                 // fipsGeocode
            "countyTaxCollectedBy",       // countyTaxCollectedBy (new field, value identical to name)
            "cityTaxCollectedBy",         // cityTaxCollectedBy (new field, value identical to name)
            "countyTaxableMax",           // countyTaxableMax (new field, value identical to name)
            "countyTaxOverMax",           // countyTaxOverMax (new field, value identical to name)
            "cityTaxableMax",             // cityTaxableMax (new field, value identical to name)
            "cityTaxOverMax"               // cityTaxOverMax (new field, value identical to name)
    );

    @Test
    public void testToString() {
        String expectedString = "InternalSalesTaxRatesMetaDataDto(" +
                "recordType=MTA, stateAbbrev=SPD, stateUseTax=Other1, countyUseTax=Other2, cityUseTax=Other3, " +
                "mtaUseTax=mtaUseTax, spdUseTax=spdUseTax, other1UseTax=other1UseTax, other2UseTax=other2UseTax, " +
                "other3UseTax=other3UseTax, other4UseTax=other4UseTax, totalUseTax=44444, countyRptCode=06, " +
                "cityRptCode=037, mtaName=mtaName, mtaNumber=0603744000, spdName=spdName, spdNumber=100000, " +
                "other1Name=other1Name, other1Number=2%, other2Name=other2Name, other2Number=50000, " +
                "other3Name=other3Name, other3Number=1%, other4Name=other4Name, other4Number=Yes, " +
                "taxShippingAlone=taxShippingAlone, taxShippingAndHandlingTogether=taxShippingAndHandlingTogether, " +
                "fipsState=06, fipsCounty=037, fipsCity=44000, fipsGeocode=0603744000, " +
                "countyTaxCollectedBy=countyTaxCollectedBy, cityTaxCollectedBy=cityTaxCollectedBy, " +
                "countyTaxableMax=countyTaxableMax, countyTaxOverMax=countyTaxOverMax, " +
                "cityTaxableMax=cityTaxableMax, cityTaxOverMax=cityTaxOverMax)";

        assertEquals(expectedString, internalSalesTaxRatesMetaDataDto.toString());
    }

}