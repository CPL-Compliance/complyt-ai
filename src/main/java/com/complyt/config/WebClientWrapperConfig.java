package com.complyt.config;

import com.complyt.business.sales_tax.sales_tax_web_clients.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.business.sales_tax.sales_tax_web_clients.ZipTaxWebClientWrapper;
import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientWrapperConfig {

    @Profile({"zipTax", "default"})
    @Bean("salesTaxWebClientWrapper")
    public ZipTaxWebClientWrapper zipTaxWebClientWrapper(WebClient webClient) {
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");
        return new ZipTaxWebClientWrapper(webClient, scheme, host, path, key);
    }

    @Profile("fastTax")
    @Bean("salesTaxWebClientWrapper")
    public FastTaxWebClientWrapper fastTaxWebClientWrapper(WebClient webClient) {
        String scheme = "https";
        String host = "trial.serviceobjects.com";
        String path = "ft/web.svc/JSON/GetBestMatch";
        Pair<String, String> key = new Pair<>("licensekey", "WS19-KRF3-JGD1");

        return new FastTaxWebClientWrapper(webClient, scheme, host, path, key);
    }

    @Profile("stubFastTax")
    @Bean("salesTaxWebClientWrapper")
    public StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper() {
        return new StubFastTaxWebClientWrapper();
    }
}
