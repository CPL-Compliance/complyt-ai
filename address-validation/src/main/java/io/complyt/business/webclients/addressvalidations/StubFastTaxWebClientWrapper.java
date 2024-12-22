package io.complyt.business.webclients.addressvalidations;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.complyt.annotations.Generated;
import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import reactor.core.publisher.Mono;

@Generated
@EqualsAndHashCode
public final class StubFastTaxWebClientWrapper extends AddressValidationWebClientWrapperBase {
    public StubFastTaxWebClientWrapper() {
        super(null, null, null, null, null);
    }

    public Mono<AddressData> validateAddress(String zip, String address, String city, String state) {
        return null;
    }

    @Override
    public Mono<AddressData> validateAddress(@NonNull Address address) {
        return Mono.fromCallable(() -> {
            String json = "{\"MatchLevel\": \"Address\",\"TaxInfoItems\": " +
                    "[{\"City\": \"Englewood\"," +
                    "\"CityDistrictRate\": \"0\"," +
                    "\"CityRate\": \"0.0\"," +
                    "\"County\": \"Arapahoe\"," +
                    "\"CountyDistrictRate\": \"0.029\"," +
                    "\"CountyRate\": \"0.0125\"," +
                    "\"InformationComponents\": [{\"Name\": \"CountyFIPS\",\"Value\": \"005\"}]," +
                    "\"NotesCodes\": \"0\"," +
                    "\"NotesDesc\": \"None\"," +
                    "\"SpecialDistrictRate\": \"0\",\"StateAbbreviation\": \"CO\"," +
                    "\"StateName\": \"Colorado\"," +
                    "\"StateRate\": \"0.0425\",\"TaxRate\": \"0.05975\"," +
                    "\"TotalTaxExempt\": \"LABOR/SERVICES\"," +
                    "\"Zip\": \"80112\"}]}";
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(json, FastTaxGetBestMatchData.class);
        });
    }
}