package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.annotations.Generated;
import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import reactor.core.publisher.Mono;

@Generated
public final class StubFastTaxWebClientWrapper extends SalesTaxWebClientWrapperBase implements SalesTaxWebClientWrapper {
    public StubFastTaxWebClientWrapper() {
        super(null, null, null, null, null, null);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        return null;
    }

    @Override
    public Mono<SalesTaxData> findByAddress(@NonNull Address address) {
        return Mono.fromCallable(() -> {
            String json = "{\"MatchLevel\": \"Address\",\"TaxInfoItems\": [{\"City\": \"Englewood\",\"CityDistrictRate\": \"0\",\"CityRate\": \"0.035\",\"County\": \"Arapahoe\",\"CountyDistrictRate\": \"0.011\",\"CountyRate\": \"0.0025\",\"InformationComponents\": [{\"Name\": \"CountyFIPS\",\"Value\": \"005\"}],\"NotesCodes\": \"1\",\"NotesDesc\": \"IsUnincorporated\",\"SpecialDistrictRate\": \"0\",\"StateAbbreviation\": \"CO\",\"StateName\": \"Colorado\",\"StateRate\": \"0.029\",\"TaxRate\": \"0.0775\",\"TotalTaxExempt\": \"LABOR/SERVICES\",\"Zip\": \"80112\"}]}";
            ObjectMapper objectMapper = new ObjectMapper();
            FastTaxData fastTaxData = objectMapper.readValue(json, FastTaxData.class);

            return fastTaxData;
        });
    }
}