package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.annotations.Generated;
import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import reactor.core.publisher.Mono;

@Generated
@EqualsAndHashCode
public final class StubFastTaxWebClientWrapper extends SalesTaxWebClientWrapperBase implements SalesTaxWebClientWrapper {
    private final String json = "{\n" +
            "    \"TaxInfoItems\": [\n" +
            "        {\n" +
            "            \"Zip\": \"95220\",\n" +
            "            \"City\": \"Acampo\",\n" +
            "            \"County\": \"San Joaquin\",\n" +
            "            \"StateName\": \"California\",\n" +
            "            \"StateAbbreviation\": \"CA\",\n" +
            "            \"TaxRate\": \"0.0775\",\n" +
            "            \"StateRate\": \"0.06\",\n" +
            "            \"CityRate\": \"0\",\n" +
            "            \"CountyRate\": \"0.0125\",\n" +
            "            \"CityDistrictRate\": \"0\",\n" +
            "            \"CountyDistrictRate\": \"0.005\",\n" +
            "            \"SpecialDistrictRate\": \"0\",\n" +
            "            \"NotesCodes\": \"\",\n" +
            "            \"NotesDesc\": \"\",\n" +
            "            \"InformationComponents\": [\n" +
            "                {\n" +
            "                    \"Name\": \"CountyFIPS\",\n" +
            "                    \"Value\": \"077\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"TotalTaxExempt\": \"LABOR/FREIGHT/SERVICES\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"MatchLevel\": \"Address\"\n" +
            "}";

    public StubFastTaxWebClientWrapper() {
        super(null, null, null, null, null);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        return null;
    }

    @Override
    public Mono<SalesTaxData> findByAddress(@NonNull Address address) {
        return Mono.fromCallable(() -> {
            ObjectMapper objectMapper = new ObjectMapper();
            FastTaxData fastTaxData = objectMapper.readValue(json, FastTaxData.class);

            return fastTaxData;
        });
    }
}