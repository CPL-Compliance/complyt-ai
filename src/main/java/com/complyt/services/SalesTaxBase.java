package com.complyt.services;

import lombok.AllArgsConstructor;
import org.javatuples.Pair;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
public abstract class SalesTaxBase {
    protected RestTemplate restTemplate;
    protected String scheme;
    protected String host;
    protected String path;
    protected Pair<String, String> licenseKey;
}