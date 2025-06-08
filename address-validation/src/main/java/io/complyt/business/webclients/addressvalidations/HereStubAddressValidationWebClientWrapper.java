package io.complyt.business.webclients.addressvalidations;

import io.complyt.annotations.Generated;
import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.here.*;
import lombok.EqualsAndHashCode;
import org.javatuples.Pair;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Generated
@EqualsAndHashCode
public class HereStubAddressValidationWebClientWrapper extends AddressValidationWebClientWrapperBase {
    public HereStubAddressValidationWebClientWrapper(WebClient webClient, String scheme, String host, String path, Pair<String, String> licenseKey) {
        super(webClient, scheme, host, path, licenseKey);
    }

    @Override
    public Mono<AddressData> validateAddress(Address address) {
        double hereScoring = address.zip().equals("00000") ? 0.1 : 0.9; // For getting both errors

        return Mono.fromCallable(() -> new HereAddressData(List.of(new HereAddressItem("", "", "", "",
                new HereAddress("Address", "USA", "US", "USA", "CA", "Los Angeles", "Beverly Hills", "1008 Elden Way", "90210"),
                new HerePosition(12345, 12345), new HereMapView(0.5, 0.5, 0.5, 0.5),
                new HereScoring(hereScoring, new HereFieldScore(1, 1, 1, List.of(1.0), 1))))));
    }
}
