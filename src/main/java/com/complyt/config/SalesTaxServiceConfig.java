package com.complyt.config;

import com.complyt.services.FastTaxService;
import com.complyt.services.ZipTaxService;
import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SalesTaxServiceConfig {

    @Profile({"zipTax", "default"})
    @Bean("salesTaxService")
    public ZipTaxService zipTaxService(RestTemplate zipTaxRestTemplate) {
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");

        return new ZipTaxService(zipTaxRestTemplate, scheme, host, path, key);
    }

    @Profile("fastTax")
    @Bean("salesTaxService")
    public FastTaxService fastTaxService(RestTemplate fastTaxRestTemplate) {
        String scheme = "https";
        String host = "trial.serviceobjects.com";
        String path = "ft/web.svc/JSON/GetBestMatch";
        Pair<String, String> key = new Pair<>("licensekey", "WS19-KRF3-JGD1");

        return new FastTaxService(fastTaxRestTemplate, scheme, host, path, key);
    }
}
