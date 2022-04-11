package com.complyt.services.sales_tax;

import lombok.AllArgsConstructor;
import org.javatuples.Pair;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
public abstract class SalesTaxBase {
    protected WebClient webClient;
    protected String scheme;
    protected String host;
    protected String path;
    protected Pair<String, String> licenseKey;
}