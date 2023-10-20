package com.complyt.config.web_clients;

import com.complyt.business.sales_tax_web_clients.*;
import com.taxjar.Taxjar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SalesTaxWebClientWrapperConfig {

    @Autowired
    private WebClientWrapperProperties fastTaxGetBestMatchWebClientWrapperProperties;

    @Autowired
    private WebClientWrapperProperties fastTaxGetByCityCountyStateWebClientWrapperProperties;

    @Autowired
    private WebClientWrapperProperties zipTaxWebClientWrapperProperties;

    @Autowired
    private WebClientWrapperProperties stubFastTaxWebClientWrapperProperties;

    @Autowired
    private WebClientWrapperProperties taxJarWebClientWrapperProperties;

    @Primary
    @Profile({"fastTax"})
    @Bean("salesTaxWebClientWrapper")
    public FastTaxGetByCityCountyStateWebClientWrapper fastTaxGetByCityCountyStateWebClientWrapper(WebClient webClient) {
        return new FastTaxGetByCityCountyStateWebClientWrapper(webClient,
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getScheme(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getHost(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getPath(),
                fastTaxGetByCityCountyStateWebClientWrapperProperties.getKey());
    }

    @Profile({"fastTax"})
    @Bean("salesTaxWebClientWrapper")
    public FastTaxGetBestMatchWebClientWrapper fastTaxWebClientWrapper(WebClient webClient) {
        return new FastTaxGetBestMatchWebClientWrapper(webClient,
                fastTaxGetBestMatchWebClientWrapperProperties.getScheme(),
                fastTaxGetBestMatchWebClientWrapperProperties.getHost(),
                fastTaxGetBestMatchWebClientWrapperProperties.getPath(),
                fastTaxGetBestMatchWebClientWrapperProperties.getKey());
    }

    @Profile({"zipTax"})
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

    @Profile({"taxJar"})
    @Bean("salesTaxWebClientWrapper")
    public TaxJarWebClientWrapper taxJarWebClientWrapper(@Value("${tax-jar-api-token}") String apiToken) {
        Taxjar client = new Taxjar(apiToken);
        return new TaxJarWebClientWrapper(client);
    }

}