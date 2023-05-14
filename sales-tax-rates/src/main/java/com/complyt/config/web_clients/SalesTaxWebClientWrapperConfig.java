package com.complyt.config.web_clients;

import com.complyt.business.sales_tax_web_clients.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.business.sales_tax_web_clients.ZipTaxWebClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SalesTaxWebClientWrapperConfig {

    @Autowired
    private WebClientWrapperProperties fastTaxWebClientWrapperProperties;

    @Autowired
    private WebClientWrapperProperties zipTaxWebClientWrapperProperties;

    @Autowired
    private WebClientWrapperProperties stubFastTaxWebClientWrapperProperties;

    @Profile({"fastTax"})
    @Bean("salesTaxWebClientWrapper")
    public FastTaxWebClientWrapper fastTaxWebClientWrapper(WebClient webClient) {
        return new FastTaxWebClientWrapper(webClient,
                fastTaxWebClientWrapperProperties.getScheme(),
                fastTaxWebClientWrapperProperties.getHost(),
                fastTaxWebClientWrapperProperties.getPath(),
                fastTaxWebClientWrapperProperties.getKey());
    }

    @Profile("zipTax")
    @Bean("salesTaxWebClientWrapper")
    public ZipTaxWebClientWrapper zipTaxWebClientWrapper(WebClient webClient) {
        return new ZipTaxWebClientWrapper(webClient,
                zipTaxWebClientWrapperProperties.getScheme(),
                zipTaxWebClientWrapperProperties.getHost(),
                zipTaxWebClientWrapperProperties.getPath(),
                zipTaxWebClientWrapperProperties.getKey());
    }

    @Profile({"stubFastTax", "default"})
    @Bean("salesTaxWebClientWrapper")
    public StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper(WebClient webClient) {
        return new StubFastTaxWebClientWrapper();
    }
}