package com.complyt.business.sales_tax;

import lombok.AllArgsConstructor;
import org.javatuples.Pair;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
public abstract class SalesTaxWebClientWrapperBase {
    protected final RestTemplate restTemplate;
    protected final WebClient webClient;
    protected final String scheme;
    protected final String host;
    protected final String path;
    protected final Pair<String, String> licenseKey;
}