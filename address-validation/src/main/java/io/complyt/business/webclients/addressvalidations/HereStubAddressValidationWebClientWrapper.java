package io.complyt.business.webclients.addressvalidations;

import io.complyt.annotations.Generated;
import io.complyt.business.address.CountryIsUsaChecker;
import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.here.*;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Generated
@EqualsAndHashCode
public class HereStubAddressValidationWebClientWrapper extends AddressValidationWebClientWrapperBase {
    public HereStubAddressValidationWebClientWrapper(WebClient webClient, String scheme, String host, String path, Pair<String, String> licenseKey) {
        super(webClient, scheme, host, path, licenseKey);
    }

    @Override
    public Mono<AddressData> validateAddress(Address address) {
        double hereScoring = address.zip().equals("00000") ? 0.1 : 0.9; // For getting both errors

        return Mono.fromCallable(() -> {
            HereAddress hereAddress;

            if ("Canada".equals(address.country())) {
                hereAddress = new HereAddress("Address", "CA", "Canada", "Canada", "Ontario", "Ontario", "Strathroy", "2994 Scotchmere Dr", "N7G 1J6");
            } else if ("Israel".equals(address.country())) {
                hereAddress = new HereAddress("Address", "IL", "Israel", "IL", "Tel aviv", "tel aviv", "tel aviv", "1008 Elden Way", "766456");
            } else if ("Ukraine".equals(address.country())) {
                hereAddress = new HereAddress("Address", "Ukraine", "Ukraine", "Ukraine", "Ukraine", "Lvov", "Lvov", "1008 Elden Way", "99999");
            } else if (CountryIsUsaChecker.isCountryUsa(address.country()) && ("AK".equals(address.state()) || "Alaska".equals(address.state()))) {
                hereAddress = new HereAddress("Address", "USA", "US", "USA", "AK", "ANCHORAGE", "ANCHORAGE", "2800 PATTERSON ST", "99504");
            } else {
                hereAddress = new HereAddress("Address", "USA", "US", "USA", "CA", "Los Angeles", "Beverly Hills", "1008 Elden Way", "90210");
            }

            HereAddressItem item = new HereAddressItem(
                    "", "", "", "",
                    hereAddress,
                    new HerePosition(12345, 12345),
                    new HereMapView(0.5, 0.5, 0.5, 0.5),
                    new HereScoring(hereScoring, new HereFieldScore(1, 1, 1, List.of(1.0), 1))
            );

            return new HereAddressData(List.of(item));
        });
    }
}
