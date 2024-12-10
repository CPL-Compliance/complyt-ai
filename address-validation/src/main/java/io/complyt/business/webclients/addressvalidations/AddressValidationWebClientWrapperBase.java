package io.complyt.business.webclients.addressvalidations;

import lombok.AllArgsConstructor;
import org.javatuples.Pair;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
public abstract class AddressValidationWebClientWrapperBase implements AddressValidationWebClientWrapper {
    protected final WebClient webClient;
    protected final String scheme;
    protected final String host;
    protected final String path;
    protected final Pair<String, String> licenseKey;
}
