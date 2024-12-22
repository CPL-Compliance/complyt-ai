package io.complyt.config.web_clients;

import io.complyt.business.webclients.addressvalidations.FastTaxGetBestMatchWebClientWrapper;
import io.complyt.business.webclients.addressvalidations.StubFastTaxWebClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class FastTaxWebClientWrapperConfig {

    @Profile({"here"})
    @Bean("fastTaxWebClientWrapper")
    public FastTaxGetBestMatchWebClientWrapper fastTaxGetBestMatchWebClientWrapper(WebClient webClient,
                                                                                   @Autowired WebClientWrapperProperties fastTaxGetBestMatchWebClientWrapperProperties) {
        return new FastTaxGetBestMatchWebClientWrapper(webClient,
                fastTaxGetBestMatchWebClientWrapperProperties.getScheme(),
                fastTaxGetBestMatchWebClientWrapperProperties.getHost(),
                fastTaxGetBestMatchWebClientWrapperProperties.getPath(),
                fastTaxGetBestMatchWebClientWrapperProperties.getKey());
    }

    @Profile({"stubHere", "default"})
    @Bean("fastTaxWebClientWrapper")
    public StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper(WebClient webClient) {
        return new StubFastTaxWebClientWrapper();
    }
}