package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class ZipTaxData implements SalesTaxData{
    public String version;
    public long rCode;
    public List<Result> results;
}

class Result {
    public String geoPostalCode;
    public String geoCity;
    public String geoCounty;
    public String geoState;
    public double taxSales;
    public double taxUse;
    public String txbService;
    public String txbFreight;
    public double stateSalesTax;
    public double stateUseTax;
    public double citySalesTax;
    public double cityUseTax;
    public String cityTaxCode;
    public double countySalesTax;
    public long countyUseTax;
    public String countyTaxCode;
    public double districtSalesTax;
    public double districtUseTax;
    public String district1Code;
    public double district1SalesTax;
    public double district1UseTax;
    public String district2Code;
    public long district2SalesTax;
    public long district2UseTax;
    public String district3Code;
    public long district3SalesTax;
    public long district3UseTax;
    public String district4Code;
    public double district4SalesTax;
    public double district4UseTax;
    public String district5Code;
    public long district5SalesTax;
    public long district5UseTax;
    public String originDestination;

}