package com.complyt.business.tax.sales_tax.sales_tax_web_clients;

import com.complyt.annotations.Generated;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.FilingMetaData;
import com.complyt.domain.sales_tax.RatesMetaData;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.MatchedAddressData;
import com.complyt.domain.transaction.Scoring;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.v1.models.matched_address.enums.FieldMatchType;
import com.complyt.v1.models.matched_address.enums.FieldsMatchScore;
import com.complyt.v1.models.matched_address.enums.MatchLevelType;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode
@Generated
public class StubComplytSalesTaxRatesClientWrapper implements SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> {

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(String state, String country, String county, String city, String street, String zip, String region, boolean isPartial, LocalDateTime transactionDate) {
        MandatoryAddress address = new MandatoryAddress(
                "Juneau", "USA", "San Joaquin", "Alaska", "2285 Trout St", "", "99801", null);
        Scoring scoring = new Scoring(MatchLevelType.EXCELLENT, 0.95, new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT));
        MatchedAddressData matchedAddressData = new MatchedAddressData(address, scoring);

        SalesTaxRates salesTaxRates = new SalesTaxRates(
                new BigDecimal("0.06"), // state
                new BigDecimal("0.0125"), // county
                new BigDecimal("0"), // city
                new BigDecimal("0.005"), // combinedDistrictRate
                new RatesMetaData(
                        new BigDecimal("0"), // cityDistrictRate
                        new BigDecimal("0.005"), // countyDistrictRate,
                        BigDecimal.ZERO
                ),
                null, // mtaRate
                null, // spdRate
                null, // otherRate
                new BigDecimal("0.0775") // taxRate
        );

        FilingMetaData filingMetaData = new FilingMetaData(
                null,                        // cityName
                "Fresno",                    // countyName
                null,                        // other1Rate
                null,                        // other2Rate
                null,                        // other3Rate
                null,                        // other4Rate
                "06",                        // countyRptCode
                "037",                       // cityRptCode
                "mtaName",                   // mtaName
                "0603744000",                // mtaNumber
                "spdName",                   // spdName
                "100000",                    // spdNumber
                "other1Name",               // other1Name
                "2%",                        // other1Number
                "other2Name",               // other2Name
                "50000",                     // other2Number
                "other3Name",               // other3Name
                "1%",                        // other3Number
                "other4Name",               // other4Name
                "Yes",                       // other4Number
                "037"                        // fipsCounty
        );

        ComplytSalesTaxRates complytSalesTaxRates = new ComplytSalesTaxRates(null, matchedAddressData, salesTaxRates, filingMetaData);
        return Mono.just(complytSalesTaxRates);
    }

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(@NonNull ShippingAddress address, LocalDateTime transactionDate) {
        return findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.region(), address.isPartial(), transactionDate);
    }

}