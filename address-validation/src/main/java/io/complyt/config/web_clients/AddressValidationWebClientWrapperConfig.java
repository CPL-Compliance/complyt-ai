package io.complyt.config.web_clients;

import io.complyt.business.webclients.addressvalidations.HereAddressValidationClientWrapper;
import io.complyt.business.webclients.addressvalidations.HereStubAddressValidationWebClientWrapper;
import lombok.NonNull;
import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AddressValidationWebClientWrapperConfig {

    @Profile({"here"})
    @Bean
    HereAddressValidationClientWrapper hereAddressValidationClientWrapper(@NonNull WebClient webClient,
                                                                          @NonNull WebClientWrapperProperties hereWebClientWrapperProperties) {
        return new HereAddressValidationClientWrapper(webClient,
                hereWebClientWrapperProperties.getScheme(),
                hereWebClientWrapperProperties.getHost(),
                hereWebClientWrapperProperties.getPath(),
                hereWebClientWrapperProperties.getKey());
    }

    @Profile({"stubHere", "default"})
    @Bean
    public HereStubAddressValidationWebClientWrapper stubHereAddressValidationWebClientWrapper() {
        return new HereStubAddressValidationWebClientWrapper(null,
                "", "", "", new Pair<>("", ""));
    }
}
