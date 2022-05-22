package com.complyt.config.web_clients;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.ZipTaxWebClientWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientWrapperConfig {

    @Profile({"zipTax", "default"})
    @Bean("salesTaxWebClientWrapper")
    public ZipTaxWebClientWrapper zipTaxWebClientWrapper(WebClient webClient) {
        return new ZipTaxWebClientWrapper(webClient,
                ZipTaxWebClientWrapperProperties.SCHEME,
                ZipTaxWebClientWrapperProperties.HOST,
                ZipTaxWebClientWrapperProperties.PATH,
                ZipTaxWebClientWrapperProperties.KEY);
    }

    @Profile("fastTax")
    @Bean("salesTaxWebClientWrapper")
    public FastTaxWebClientWrapper fastTaxWebClientWrapper(WebClient webClient) {
        return new FastTaxWebClientWrapper(webClient,
                FastTaxWebClientWrapperProperties.SCHEME,
                FastTaxWebClientWrapperProperties.HOST,
                FastTaxWebClientWrapperProperties.PATH,
                FastTaxWebClientWrapperProperties.KEY);
    }

    @Profile("stubFastTax")
    @Bean("salesTaxWebClientWrapper")
    public StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper() {
        return new StubFastTaxWebClientWrapper();
    }
}