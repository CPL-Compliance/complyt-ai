package com.complyt.business.sales_tax_web_clients;

import lombok.AllArgsConstructor;
import org.javatuples.Pair;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
public abstract class SalesTaxWebClientWrapperBase implements SalesTaxWebClientWrapper {
    protected final WebClient webClient;
    protected final String scheme;
    protected final String host;
    protected final String path;
    protected final Pair<String, String> licenseKey;
}