package com.complyt.config;

import com.complyt.business.sales_tax.FastTaxWebClientWrapper;
import com.complyt.business.sales_tax.ZipTaxWebClientWrapper;
import com.complyt.domain.sales_tax.SalesTaxCalculator;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import com.complyt.services.SalesTaxService;
import com.complyt.services.SalesTaxServiceImpl;
import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientWrapperConfig {

    @Profile({"zipTax", "default"})
    @Bean("salesTaxWebClientWrapper")
    public ZipTaxWebClientWrapper zipTaxWebClientWrapper(WebClient zipTaxWebClient) {
        String scheme = "https";
        String host = "api.zip-tax.com";
        String path = "request/v40";
        Pair<String, String> key = new Pair<>("key", "jkRvcDF9MVB5pxtm");
        RestTemplate restTemplate = new RestTemplate();
        return new ZipTaxWebClientWrapper(restTemplate,zipTaxWebClient, scheme, host, path, key);
    }

    @Profile("fastTax")
    @Bean("salesTaxWebClientWrapper")
    public FastTaxWebClientWrapper fastTaxWebClientWrapper(WebClient fastTaxWebClient) {
        String scheme = "https";
        String host = "trial.serviceobjects.com";
        String path = "ft/web.svc/JSON/GetBestMatch";
        Pair<String, String> key = new Pair<>("licensekey", "WS19-KRF3-JGD1");
        RestTemplate restTemplate = new RestTemplate();
        return new FastTaxWebClientWrapper(restTemplate,fastTaxWebClient, scheme, host, path, key);
    }
}
