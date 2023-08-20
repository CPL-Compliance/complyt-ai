package com.complyt.domain.zip_tax;

import lombok.Builder;

@Builder
public record Result(String geoPostalCode, String geoCity, String geoCounty, String geoState, double taxSales,
                     double taxUse, String txbService, String txbFreight, double stateSalesTax, double stateUseTax,
                     double citySalesTax, double cityUseTax, String cityTaxCode, double countySalesTax,
                     long countyUseTax, String countyTaxCode, double districtSalesTax, double districtUseTax,
                     String district1Code, double district1SalesTax, double district1UseTax, String district2Code,
                     long district2SalesTax, long district2UseTax, String district3Code, long district3SalesTax,
                     long district3UseTax, String district4Code, double district4SalesTax, double district4UseTax,
                     String district5Code, long district5SalesTax, long district5UseTax, String originDestination) {
}