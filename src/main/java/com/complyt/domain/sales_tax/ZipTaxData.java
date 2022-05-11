package com.complyt.domain.sales_tax;

import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ZipTaxData implements SalesTaxData {
    private String version;
    private long rCode;
    private List<Result> results;
}

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
class Result {
    private String geoPostalCode;
    private String geoCity;
    private String geoCounty;
    private String geoState;
    private double taxSales;
    private double taxUse;
    private String txbService;
    private String txbFreight;
    private double stateSalesTax;
    private double stateUseTax;
    private double citySalesTax;
    private double cityUseTax;
    private String cityTaxCode;
    private double countySalesTax;
    private long countyUseTax;
    private String countyTaxCode;
    private double districtSalesTax;
    private double districtUseTax;
    private String district1Code;
    private double district1SalesTax;
    private double district1UseTax;
    private String district2Code;
    private long district2SalesTax;
    private long district2UseTax;
    private String district3Code;
    private long district3SalesTax;
    private long district3UseTax;
    private String district4Code;
    private double district4SalesTax;
    private double district4UseTax;
    private String district5Code;
    private long district5SalesTax;
    private long district5UseTax;
    private String originDestination;
}